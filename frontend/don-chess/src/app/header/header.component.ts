import { Component, OnInit, OnDestroy } from '@angular/core';
import { AuthService } from '../auth/auth.service';
import { Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html'
})
export class HeaderComponent implements OnInit, OnDestroy {
  isAuthenticated = false;
  userName = '';

  authChangedSubs: Subscription;
  userNameSubs: Subscription;


  constructor(private authService: AuthService, private router: Router, private http: HttpClient ) { }

  ngOnInit() {
    this.authChangedSubs = this.authService.authenticatedChanged.subscribe(auth => this.isAuthenticated = auth);
    this.userNameSubs = this.authService.userName.subscribe(name => {
      this.userName = name.fullName;
    });
  }

  onLogout() {
    this.authService.onLogout();
  }

  ngOnDestroy() {
    this.authChangedSubs.unsubscribe();
    this.userNameSubs.unsubscribe();
  }
}
