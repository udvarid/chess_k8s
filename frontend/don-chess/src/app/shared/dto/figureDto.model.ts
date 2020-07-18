import { ChessFigure, Color } from '../enums/enums.model';

export interface FigureDto {
    figureType: ChessFigure;
    color: Color;
    coordX: number;
    coordY: number;
}
