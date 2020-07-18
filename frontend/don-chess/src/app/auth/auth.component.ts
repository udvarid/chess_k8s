import { Component, OnInit, OnDestroy } from '@angular/core';
import { NgForm } from '@angular/forms';
import { Router } from '@angular/router';

import { AuthService } from './auth.service';
import { UserLoginDto } from '../shared/dto/userLoginDto.model';
import { RegisterDto } from '../shared/dto/registerDto.model';
import { Subscription } from 'rxjs';
import { ToastrService } from 'ngx-toastr';
import { CommonService } from '../shared/service/common.service';


@Component({
  selector: 'app-auth',
  templateUrl: './auth.component.html'
})
export class AuthComponent implements OnInit, OnDestroy {


  isLoginMode = true;
  isLoading = false;
  error: string = null;
  private authSubs: Subscription = new Subscription();
  private endLoadingSigner: Subscription = new Subscription();

  constructor(private authService: AuthService,
              private router: Router,
              private toastrService: ToastrService,
              private commonService: CommonService) {
    this.authService.authenticate();
  }

  ngOnInit() {
    this.authSubs = this.authService.authenticatedSign.subscribe( (response: boolean) => {
      if (response === true) {
        this.toastrService.success('Succesfull login');
        this.router.navigate(['/info']);
      }
    });
    this.endLoadingSigner = this.commonService.httpAnswer.subscribe( (response: boolean) => {
      this.isLoading = false;
    }

    );
  }

  ngOnDestroy() {
    this.authSubs.unsubscribe();
    this.endLoadingSigner.unsubscribe();
  }


  onSwitchMode() {
    this.isLoginMode = !this.isLoginMode;
  }

  onSubmit(form: NgForm) {
    if (!form.valid) {
      return;
    }

    this.isLoading = true;

    if (this.isLoginMode) {
      this.toastrService.info('Login attempt');
      const userLogin: UserLoginDto = {
        password: form.value.password,
        username: form.value.email,
      };
      this.authService.onLogin(userLogin).subscribe( () => {
        this.authService.authenticate();
      });
    } else {
      this.toastrService.info('Registration sent, you will receive a confirmation email!', '', {
        timeOut: 5000
      });
      const userRegister: RegisterDto = {
        email: form.value.email,
        password: form.value.password,
        fullName: form.value.fullName,
      };
      this.authService.onRegister(userRegister).subscribe( () => {
        this.router.navigate(['/info']);
      })
      ;
    }

    form.reset();
  }

  authenticated() { return this.authService.authenticated; }


}
