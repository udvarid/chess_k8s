import { CoordinateDto } from './coordinateDto.model';

export interface ValidMovesDto {
    chessGameId: number;
    validMoves: CoordinateDto[];
}
