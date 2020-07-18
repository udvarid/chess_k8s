import { UserDto } from './userDto.model';
import { ChessGameType, ChessGameStatus, Result, Color } from '../enums/enums.model';
import { FigureDto } from './figureDto.model';

export interface ChessTableDto {
    chessGameId: number;
    userOne: UserDto;
    userTwo: UserDto;
    chessGameType: ChessGameType;
    chessGameStatus: ChessGameStatus;
    result: Result;
    nextMove: Color;
    lastMoveId: number;
    figures: FigureDto[];
    drawOffered: boolean;
    moveHistory: string;
}
