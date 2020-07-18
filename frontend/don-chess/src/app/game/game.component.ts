import { Component, OnInit, OnDestroy } from '@angular/core';
import { Subscription } from 'rxjs';
import { GameService } from './game.service';
import { ChessTableDto } from '../shared/dto/chessTableDto.model';
import { ChessGameDto } from '../shared/dto/chessGameDto.model';
import { Result } from '../shared/enums/enums.model';
// import { ConsoleReporter } from 'jasmine';
// import { WebSocketService } from '../shared/service/web-socket.service';
// import { environment } from 'src/environments/environment';

@Component({
  selector: 'app-game',
  templateUrl: './game.component.html',
  styleUrls: ['./game.component.css']
})
export class GameComponent implements OnInit, OnDestroy {

  public gameSelected: ChessTableDto = null;
  private selectedGameSubs: Subscription;
  private eogSubs: Subscription;
  private chessTableLoadSign: Subscription;

  public chessTableLoaded = false;
  pre: string;
  private moveHappend = -1;
  private timer: any;

  constructor(private gameService: GameService/* , private webSocketService: WebSocketService */) {
/*     this.pre = environment.apiUrl;
    const stompClient = this.webSocketService.connect();
    stompClient.connect({}, frame => {
      stompClient.subscribe(this.pre + '/topic/notification', (notifications) => {
      this.moveHappend = notifications.body;
      if (this.moveHappend !== -1 &&
          this.gameSelected !== null &&
          this.gameSelected.chessGameId !== null &&
          this.moveHappend - this.gameSelected.chessGameId === 0) {
            this.gameService.getGameSelected(this.gameSelected.chessGameId);
            this.gameService.getGameList();
      }
    });

    }); */

    }

ngOnInit() {
    this.selectedGameSubs = this.gameService.gameSelectedChange.subscribe((game: ChessTableDto) => {

      if (this.gameSelected !== null) {
         clearInterval(this.timer);
      }
      const checkIn = () => this.gameService.checkInWithCurrentGame(game.chessGameId, game.lastMoveId);
      this.timer = setInterval(checkIn, 2000);
      this.gameSelected = game;
    });
    this.eogSubs = this.gameService.endOfGameResult.subscribe(() => {
      this.gameSelected = null;
      clearInterval(this.timer);
    });
    this.chessTableLoadSign = this.gameService.chessTableLoaded.subscribe( () => {
      this.chessTableLoaded = true;
    });

  }

ngOnDestroy() {
    this.selectedGameSubs.unsubscribe();
    this.eogSubs.unsubscribe();
    this.chessTableLoadSign.unsubscribe();
    if (this.gameSelected !== null) {
      clearInterval(this.timer);
    }
  }

amINext(game: ChessGameDto): boolean {
    return this.gameService.amINext(game) && game.result === Result.Open;
  }

onResign() {
    this.gameService.resign(this.gameSelected.chessGameId);
  }

onDrawOffer() {
    this.gameService.offerDraw();
  }

onAcceptDraw() {
    this.gameService.acceptDraw(this.gameSelected.chessGameId);
  }

isDrawOffered(): boolean {
    return this.gameService.drawOffered;
  }

}
