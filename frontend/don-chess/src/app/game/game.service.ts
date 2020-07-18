import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Router } from '@angular/router';
import { ChessGameDto } from '../shared/dto/chessGameDto.model';
import { Subject } from 'rxjs';
import { ChessTableDto } from '../shared/dto/chessTableDto.model';
import { ValidMovesDto } from '../shared/dto/validMovesDto.model';
import { Cell, CellTarget } from '../shared/chessTable.model';
import { ChessMoveDto } from '../shared/dto/chessMoveDto.model';
import { FigureDto } from '../shared/dto/figureDto.model';
import { Color, PromoteType, ChessFigure, Result } from '../shared/enums/enums.model';
import { CoordinateDto } from '../shared/dto/coordinateDto.model';
import { AuthService } from '../auth/auth.service';
import { environment } from 'src/environments/environment';
import { ResultDto } from '../shared/dto/resultDto.model';
import { ToastrService } from 'ngx-toastr';

@Injectable({ providedIn: 'root' })

export class GameService {

    private games: ChessGameDto[] = [];
    public gameSelected: ChessTableDto;
    private gameValidMoves: ValidMovesDto;
    private selectedCell: Cell = null;
    private table: Cell[][] = [];
    private promotedFigure: PromoteType = PromoteType.Queen;
    gamesChanged = new Subject<ChessGameDto[]>();
    gameSelectedChange = new Subject<ChessTableDto>();
    gameValidMovesChange = new Subject<ValidMovesDto>();
    chessTableChanged = new Subject<Cell[][]>();
    endOfGameResult = new Subject<ResultDto>();
    newPromotion = new Subject<boolean>();
    chessTableLoaded = new Subject<boolean>();
    pre: string;
    drawOffered = false;

    constructor(private http: HttpClient, private router: Router, private authService: AuthService, private toastrService: ToastrService) {
        this.pre = environment.apiUrl;
    }

    getGameList() {
        const header = new HttpHeaders({});
        this.http.get(this.pre + '/api/game/list', {headers: header,  withCredentials: true }).subscribe((response: ChessGameDto[]) => {
            this.games = response;
            this.gamesChanged.next(response);
        });
    }

    setPromotion(promotion: PromoteType) {
        this.promotedFigure = promotion;
        this.newPromotion.next(true);
    }

    getGameSelected(index: number) {
        const header = new HttpHeaders({});
        this.http.get(this.pre + '/api/game/' + index, {headers: header,  withCredentials: true }).subscribe((response: ChessTableDto) => {
            this.gameSelected = response;
            this.gameSelectedChange.next(response);
            this.drawOffered = false;
            this.getGameValidMoves(index);
        });
    }

    getGameValidMoves(index: number) {
        const header = new HttpHeaders({});
        this.http.get(this.pre + '/api/game/validMoves/' + index, {headers: header,  withCredentials: true })
        .subscribe((response: ValidMovesDto) => {
            this.gameValidMoves = response;
            this.gameValidMovesChange.next(response);
            this.createTable();
        });
    }

    makeMove(move: ChessMoveDto) {
        const header = new HttpHeaders({});
        this.http.post(this.pre + '/api/game/move', move, {headers: header,  withCredentials: true })
        .subscribe((result: ResultDto) => {
            this.toastrService.info('Valid move sent', '', {
                timeOut: 5000
            });
            this.getGameSelected(this.gameSelected.chessGameId);
            if (result.result !== Result.Open) {
                this.endOfGameResult.next(result);
            }
            this.getGameList();
        });
    }

    clickOnCell(clickedCell: Cell) {
        if (clickedCell.targets.length > 0) {
            this.selectedCell = clickedCell;
            this.createTable();
            return;
        }
        if (this.selectedCell === null) {
            return;
        }
        if (this.selectedCell.targets.find(cell =>
            cell.coordX === clickedCell.coordX && cell.coordY === clickedCell.coordY
        )) {
            const move: ChessMoveDto = {
                gameId: this.gameSelected.chessGameId,
                moveId: this.gameSelected.lastMoveId + 1,
                moveFromX: this.selectedCell.coordY + 1,
                moveFromY: this.selectedCell.coordX + 1,
                moveToX: clickedCell.coordY + 1,
                moveToY: clickedCell.coordX + 1,
                promoteToFigure: this.canBePromoted(clickedCell) ? this.promotedFigure : '',
                drawOffered: this.drawOffered
            };
            this.selectedCell = null;
            this.makeMove(move);
        } else {
            this.selectedCell = null;
            this.createTable();
        }
    }

    canBePromoted(clickedCell: Cell): boolean {
        if (this.selectedCell !== null && this.selectedCell.chessFigure === ChessFigure.Pawn) {
            if (this.selectedCell.chessFigureColor === Color.White && clickedCell.coordX === 7 ||
                this.selectedCell.chessFigureColor === Color.Black && clickedCell.coordX === 0) {
                return true;
            }
        }
        return false;
    }

    createTable() {
        const amINextPlayer: boolean = this.amINext(this.gameSelected);
        const selectedTargets: CellTarget[] = this.selectedCell ?
                                              this.getTargets(this.selectedCell.coordX + 1, this.selectedCell.coordY + 1) : [];
        for (let i = 0; i < 8; i++) {
          this.table[i] = [];
          for (let j = 0; j < 8; j++) {
            const figure: FigureDto = this.getFigure(7 - i + 1, j + 1);
            const targets: CellTarget[] = amINextPlayer ? this.getTargets(7 - i + 1, j + 1) : null;
            this.table[i][j] = {
              coordX: 7 - i,
              coordY: j,
              color: (7 - i + j) % 2 === 0 ? Color.Black : Color.White,
              chessFigure: figure ? figure.figureType : null,
              chessFigureColor: figure ? figure.color : null,
              targets: targets ? targets : [],
              selectedCell: this.selectedCell &&
                            this.selectedCell.coordX === 7 - i &&
                            this.selectedCell.coordY === j ? true : false,
              targetCell: selectedTargets.find(cell => cell.coordX === 7 - i && cell.coordY === j) ? true : false
            };
          }
        }
        this.chessTableChanged.next(this.table);
      }

      getFigure(row: number, column: number): FigureDto {
          const figure: FigureDto = this.gameSelected.figures.find(fig => fig.coordX === column && fig.coordY === row);
          return figure;
      }

      getTargets(row: number, column: number): CellTarget[] {
        const coordinates: CoordinateDto[] =
            this.gameValidMoves.validMoves.filter(target => target.fromX === column && target.fromY === row);

        const targets: CellTarget[] = coordinates.map(coord => {
          const target: CellTarget = {
            coordX: coord.toY - 1,
            coordY: coord.toX - 1
          };
          return target;
        });

        return targets;
      }

      amINext(game: ChessGameDto): boolean {
        if (game.nextMove === Color.White && game.userOne.id === this.authService.getUserName().id ||
            game.nextMove === Color.Black && game.userTwo.id === this.authService.getUserName().id) {
          return true;
        }
        return false;
      }

      resign(gameId: number) {
        const header = new HttpHeaders({});
        this.http.get(this.pre + '/api/game/giveUp/' + gameId, {headers: header,  withCredentials: true })
        .subscribe((result: ResultDto) => {
            this.endOfGameResult.next(result);
        });
      }

      acceptDraw(gameId: number) {
        const header = new HttpHeaders({});
        this.http.get(this.pre + '/api/game/acceptDraw/' + gameId, {headers: header,  withCredentials: true })
        .subscribe((result: ResultDto) => {
            this.endOfGameResult.next(result);
        });
      }


      offerDraw() {
        this.drawOffered = !this.drawOffered;
      }

      getActualColor(): Color {
          return this.gameSelected.nextMove;
      }

      niceResult(result: ResultDto): string {
        let niceResult: string;
        if (result.result === Result.Drawn) {
          niceResult = 'Drawn';
        } else if (result.result === Result.Won_User_One) {
          niceResult = 'Winner is ' + result.userOne;
        } else if (result.result === Result.Won_User_Two) {
           niceResult = 'Winner is ' + result.userTwo;
        } else {
           niceResult = 'Open';
        }
        return niceResult;
      }

      thisIsLoaded() {
        this.chessTableLoaded.next(true);
      }

      checkInWithCurrentGame(gameId: number, lastMoveId: number) {
        const header = new HttpHeaders({});
        this.http.get(this.pre + '/api/game/' + gameId, {headers: header,  withCredentials: true }).subscribe((response: ChessTableDto) => {
          if (response.lastMoveId !== lastMoveId) {
            this.gameSelected = response;
            this.gameSelectedChange.next(response);
            this.drawOffered = false;
            this.getGameValidMoves(gameId);
            this.getGameList();
          }
      });
      }

}
