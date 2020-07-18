export interface ChessMoveDto {
    gameId: number;
    moveId: number;
    moveFromX: number;
    moveFromY: number;
    moveToX: number;
    moveToY: number;
    promoteToFigure: string;
    drawOffered: boolean;
}
