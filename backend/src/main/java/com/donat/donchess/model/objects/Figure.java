package com.donat.donchess.model.objects;

import com.donat.donchess.model.enums.ChessFigure;
import com.donat.donchess.model.enums.Color;

import java.util.Objects;

public class Figure {

    private ChessFigure figureType;

    private Color color;

    private int coordX;

    private int coordY;

    private int initCoordX;

    private int initCoordY;

    private boolean moved;

    public Figure(ChessFigure figureType, Color color, int coordX, int coordY) {
        this.figureType = figureType;
        this.color = color;
        this.coordX = coordX;
        this.coordY = coordY;
        this.moved = false;
        this.initCoordX = coordX;
        this.initCoordY = coordY;
    }

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

    public int getInitCoordX() {
        return initCoordX;
    }

    public int getInitCoordY() {
        return initCoordY;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Figure figure = (Figure) o;
        return coordX == figure.coordX &&
                coordY == figure.coordY &&
                figureType == figure.figureType &&
                color == figure.color;
    }


    @Override
    public int hashCode() {
        return Objects.hash(initCoordX,initCoordY);
    }
}
