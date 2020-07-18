import { UserDto } from './userDto.model';
import { ChessGameType, ChessGameStatus, Result, Color } from '../enums/enums.model';

export interface ChessGameDto {
    chessGameId: number;
    userOne: UserDto;
    userTwo: UserDto;
    chessGameType: ChessGameType;
    chessGameStatus: ChessGameStatus;
    result: Result;
    nextMove: Color;
    lastMoveId: number;
}
