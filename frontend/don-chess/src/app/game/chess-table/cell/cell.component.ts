import { Component, OnInit, Input } from '@angular/core';
import { Cell } from 'src/app/shared/chessTable.model';
import { Color, ChessFigure } from 'src/app/shared/enums/enums.model';

@Component({
  selector: 'app-cell',
  templateUrl: './cell.component.html',
  styleUrls: ['./cell.component.css']
})
export class CellComponent {

  @Input() public cell: Cell;

  private fileFolder = 'assets/picture/';

  constructor() { }

  getFileName(): string {
    let fileName: string;
    if (this.cell.chessFigure) {
      if (this.cell.chessFigure === ChessFigure.Pawn) {
        fileName = this.fileFolder.concat(this.cell.chessFigureColor === Color.Black ? 'PawnBlack.png' : 'PawnWhite.png');
      }
      if (this.cell.chessFigure === ChessFigure.Rook) {
        fileName = this.fileFolder.concat(this.cell.chessFigureColor === Color.Black ? 'RookBlack.png' : 'RookWhite.png');
      }
      if (this.cell.chessFigure === ChessFigure.Knight) {
        fileName = this.fileFolder.concat(this.cell.chessFigureColor === Color.Black ? 'KnightBlack.png' : 'KnightWhite.png');
      }
      if (this.cell.chessFigure === ChessFigure.Bishop) {
        fileName = this.fileFolder.concat(this.cell.chessFigureColor === Color.Black ? 'BishopBlack.png' : 'BishopWhite.png');
      }
      if (this.cell.chessFigure === ChessFigure.Queen) {
        fileName = this.fileFolder.concat(this.cell.chessFigureColor === Color.Black ? 'QueenBlack.png' : 'QueenWhite.png');
      }
      if (this.cell.chessFigure === ChessFigure.King) {
        fileName = this.fileFolder.concat(this.cell.chessFigureColor === Color.Black ? 'KingBlack.png' : 'KingWhite.png');
      }
    }

    return fileName;

  }

  getBackgroundColor(): string {
    return this.cell.color === Color.Black ? 'rgb(134, 132, 132)' : 'white';
  }

  getBorderColor(): string {
    if (this.cell.selectedCell) {
      return 'red';
    }
    if (this.cell.targetCell) {
      return 'yellow';
    }
    return 'black';
  }


}
