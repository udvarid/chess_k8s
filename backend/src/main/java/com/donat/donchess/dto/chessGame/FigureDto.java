package com.donat.donchess.dto.chessGame;

import com.donat.donchess.model.enums.ChessFigure;
import com.donat.donchess.model.enums.Color;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

public class FigureDto {

    @Enumerated(EnumType.STRING)
    private ChessFigure figureType;

    @Enumerated(EnumType.STRING)
    private Color color;

    private int coordX;

    private int coordY;

    private boolean moved;

    public ChessFigure getFigureType() {
        return figureType;
    }

    public void setFigureType(ChessFigure figureType) {
        this.figureType = figureType;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public int getCoordX() {
        return coordX;
    }

    public void setCoordX(int coordX) {
        this.coordX = coordX;
    }

    public int getCoordY() {
        return coordY;
    }

    public void setCoordY(int coordY) {
        this.coordY = coordY;
    }

    public boolean isMoved() {
        return moved;
    }

    public void setMoved(boolean moved) {
        this.moved = moved;
    }

    @Override
    public String toString() {
        return "FigureDto{" +
                "figureType=" + figureType +
                ", color=" + color +
                ", coordX=" + coordX +
                ", coordY=" + coordY +
                '}';
    }
}
