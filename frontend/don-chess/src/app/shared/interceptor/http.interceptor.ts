import { HttpInterceptor, HttpHandler, HttpRequest, HttpEvent, HttpResponse, HttpErrorResponse } from '@angular/common/http';

import { catchError, tap } from 'rxjs/operators';
import { ToastrService } from 'ngx-toastr';
import { Observable, EMPTY, Subject } from 'rxjs';
import { Injectable } from '@angular/core';
import { CommonService } from '../service/common.service';

@Injectable()
export class AppHttpInterceptor implements HttpInterceptor {
    public httpIsBack: Subject<boolean> = new Subject<boolean>();
    constructor(public toasterService: ToastrService, private commonService: CommonService) {}

    intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
        return next.handle(request).pipe(
            tap((ans) => {
              if (ans instanceof HttpResponse) {
                if(ans.body && ans.body.success) {
                  this.commonService.signNewAnswer();
                }
               }}),
            catchError((error: HttpErrorResponse) => {
                if (error.error.message !== undefined && error.error.message !== '') {
                  this.commonService.signNewAnswer();
                  this.toasterService.warning(error.error.message, '', {
                    timeOut: 5000
                  });
                }
                if (error.url.substr(error.url.length - 5) === 'login') {
                  this.commonService.signNewAnswer();
                  this.toasterService.warning('Unsuccesfull login', '', {
                    timeOut: 5000
                  });
                }
                return EMPTY;
          })
        );
      }


}
