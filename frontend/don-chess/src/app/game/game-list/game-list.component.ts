import { Component, OnInit, OnDestroy } from '@angular/core';
import { ChessGameDto } from 'src/app/shared/dto/chessGameDto.model';
import { Subscription } from 'rxjs';
import { GameService } from '../game.service';
import { ChessGameStatus, Color, Result } from 'src/app/shared/enums/enums.model';
import { ResultDto } from 'src/app/shared/dto/resultDto.model';
import { ToastrService } from 'ngx-toastr';


@Component({
  selector: 'app-game-list',
  templateUrl: './game-list.component.html',
  styleUrls: ['./game-list.component.css']
})
export class GameListComponent implements OnInit, OnDestroy {

  private games: ChessGameDto[];
  public openGames: ChessGameDto[];
  public closedGames: ChessGameDto[];
  private gameChangedSubs: Subscription;
  private eogSubs: Subscription;
  public endOfGame: ResultDto = {
    result: Result.Open,
    userOne: '',
    userTwo: '',
  };
  public isLoading = false;
  public activeShown = true;

  public selectedIndex = -1;


  constructor(private gameService: GameService, private toastrService: ToastrService) { }

  ngOnInit() {
    this.gameChangedSubs = this.gameService.gamesChanged.subscribe((response: ChessGameDto[]) => {
      this.openGames = response.filter(game => game.chessGameStatus === ChessGameStatus.Open);
      this.closedGames = response.filter(game => game.chessGameStatus !== ChessGameStatus.Open);
      this.openGames = this.openGames.filter(game => this.amINext(game))
                  .concat(this.openGames.filter(game => !this.amINext(game)));
      this.isLoading = false;
      this.games = this.openGames.slice();
    });
    this.eogSubs = this.gameService.endOfGameResult.subscribe((result: ResultDto) => {
        this.endOfGame = result;
        this.toastrService.warning(this.gameService.niceResult(this.endOfGame), '', {
          timeOut: 5000
        });
        this.isLoading = true;
        this.gameService.getGameList();
    });
    this.isLoading = true;
    this.gameService.getGameList();
  }

  onSelect(index: number) {
    this.gameService.getGameSelected(this.games[index].chessGameId);
    this.selectedIndex = index;
  }

  amINext(game: ChessGameDto): boolean {
    return this.gameService.amINext(game);
  }

  ngOnDestroy() {
    this.gameChangedSubs.unsubscribe();
    this.eogSubs.unsubscribe();
  }

  onActiveGames() {
    this.games = this.openGames.slice();
    this.activeShown = true;
    this.selectedIndex = -1;
  }

  onClosedGames() {
    this.games = this.closedGames.slice();
    this.activeShown = false;
    this.selectedIndex = -1;
  }

  niceResult(game: ChessGameDto): string {

    const result: ResultDto = {
      result: game.result,
      userOne: game.userOne.fullName,
      userTwo: game.userTwo.fullName
    };
    return this.gameService.niceResult(result);
  }

}
