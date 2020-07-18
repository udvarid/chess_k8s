import { BrowserModule } from '@angular/platform-browser';
import { NgModule, Injectable } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { HttpClientModule, HttpInterceptor, HttpHandler, HttpRequest, HTTP_INTERCEPTORS } from '@angular/common/http';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { HeaderComponent } from './header/header.component';
import { ChallengeComponent } from './challenge/challenge.component';
import { GameComponent } from './game/game.component';
import { AuthComponent } from './auth/auth.component';
import { InfoComponent } from './info/info.component';
import { LoadingSpinnerComponent } from './shared/loading-spinner/loading-spinner.component';
import { DropdownDirective } from './shared/dropdown.directives';
import { AuthService } from './auth/auth.service';
import { UserListComponent } from './challenge/user-list/user-list.component';
import { ChallengeListComponent } from './challenge/challenge-list/challenge-list.component';
import { GameListComponent } from './game/game-list/game-list.component';
import { ChessTableComponent } from './game/chess-table/chess-table.component';
import { CellComponent } from './game/chess-table/cell/cell.component';
import { ModalComponent } from './shared/modal/modal.component';
import { ToastrModule } from 'ngx-toastr';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { AppHttpInterceptor } from './shared/interceptor/http.interceptor';
// import { WebSocketService } from './shared/service/web-socket.service';


@Injectable()
export class XhrInterceptor implements HttpInterceptor {

  intercept(req: HttpRequest<any>, next: HttpHandler) {
    const xhr = req.clone({
      headers: req.headers.set('X-Requested-With', 'XMLHttpRequest')
    });
    return next.handle(xhr);
  }
}

@NgModule({
  declarations: [
    AppComponent,
    HeaderComponent,
    ChallengeComponent,
    GameComponent,
    AuthComponent,
    DropdownDirective,
    InfoComponent,
    LoadingSpinnerComponent,
    UserListComponent,
    ChallengeListComponent,
    GameListComponent,
    ChessTableComponent,
    CellComponent,
    ModalComponent
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    AppRoutingModule,
    FormsModule,
    HttpClientModule,
    ReactiveFormsModule,
    ToastrModule.forRoot({
      timeOut: 2000,
      positionClass: 'toast-bottom-right',
      preventDuplicates: true,
    })
  ],
  providers: [AuthService,
              // WebSocketService,
             { provide: HTTP_INTERCEPTORS, useClass: XhrInterceptor, multi: true },
             { provide: HTTP_INTERCEPTORS, useClass: AppHttpInterceptor, multi: true }
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
