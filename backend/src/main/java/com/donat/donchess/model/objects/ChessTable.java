package com.donat.donchess.model.objects;

import com.donat.donchess.model.enums.Color;

import java.util.HashSet;
import java.util.Set;

public class ChessTable {

    private Long chessGameId;

    private Set<Figure> figures = new HashSet<>();

    private Color whoIsNext;

    private int actualMoveNumber;

    private int lastHitMoveNumber;

    private int lastPawnMoveNumber;

    private boolean lastMoveWasDoublePawn;

    private int columnIndexIfLastMoveWasDoublePawn;

    private boolean drawWasOffered;

    private String moveHistory;

    public Long getChessGameId() {
        return chessGameId;
    }

    public void setChessGameId(Long chessGameId) {
        this.chessGameId = chessGameId;
    }

    public Set<Figure> getFigures() {
        return figures;
    }

    public void setFigures(Set<Figure> figures) {
        this.figures = figures;
    }

    public Color getWhoIsNext() {
        return whoIsNext;
    }

    public void setWhoIsNext(Color whoIsNext) {
        this.whoIsNext = whoIsNext;
    }

    public int getActualMoveNumber() {
        return actualMoveNumber;
    }

    public void setActualMoveNumber(int actualMoveNumber) {
        this.actualMoveNumber = actualMoveNumber;
    }

    public int getLastHitMoveNumber() {
        return lastHitMoveNumber;
    }

    public void setLastHitMoveNumber(int lastHitMoveNumber) {
        this.lastHitMoveNumber = lastHitMoveNumber;
    }

    public int getLastPawnMoveNumber() {
        return lastPawnMoveNumber;
    }

    public void setLastPawnMoveNumber(int lastPawnMoveNumber) {
        this.lastPawnMoveNumber = lastPawnMoveNumber;
    }

    public boolean isLastMoveWasDoublePawn() {
        return lastMoveWasDoublePawn;
    }

    public void setLastMoveWasDoublePawn(boolean lastMoveWasDoublePawn) {
        this.lastMoveWasDoublePawn = lastMoveWasDoublePawn;
    }

    public int getColumnIndexIfLastMoveWasDoublePawn() {
        return columnIndexIfLastMoveWasDoublePawn;
    }

    public void setColumnIndexIfLastMoveWasDoublePawn(int columnIndexIfLastMoveWasDoublePawn) {
        this.columnIndexIfLastMoveWasDoublePawn = columnIndexIfLastMoveWasDoublePawn;
    }

    public boolean isDrawWasOffered() {
        return drawWasOffered;
    }

    public void setDrawWasOffered(boolean drawWasOffered) {
        this.drawWasOffered = drawWasOffered;
    }

    public String getMoveHistory() {
        return moveHistory;
    }

    public void setMoveHistory(String moveHistory) {
        this.moveHistory = moveHistory;
    }
}
