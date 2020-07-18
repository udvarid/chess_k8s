package com.donat.donchess.dto.chessGame;

import com.donat.donchess.domain.enums.ChessGameStatus;
import com.donat.donchess.domain.enums.ChessGameType;
import com.donat.donchess.domain.enums.Result;
import com.donat.donchess.dto.User.UserDto;
import com.donat.donchess.model.enums.Color;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Lob;
import java.util.HashSet;
import java.util.Set;

public class ChessTableDto {

    private Long chessGameId;

    private UserDto userOne;

    private UserDto userTwo;

    @Enumerated(EnumType.STRING)
    private ChessGameType chessGameType;

    @Enumerated(EnumType.STRING)
    private ChessGameStatus chessGameStatus;

    @Enumerated(EnumType.STRING)
    private Result result;

    @Enumerated(EnumType.STRING)
    private Color nextMove;

    private int lastMoveId;

    private Set<FigureDto> figures = new HashSet<>();

    private boolean drawOffered;

    @Lob
    private String moveHistory;

    public Long getChessGameId() {
        return chessGameId;
    }

    public void setChessGameId(Long chessGameId) {
        this.chessGameId = chessGameId;
    }

    public UserDto getUserOne() {
        return userOne;
    }

    public void setUserOne(UserDto userOne) {
        this.userOne = userOne;
    }

    public UserDto getUserTwo() {
        return userTwo;
    }

    public void setUserTwo(UserDto userTwo) {
        this.userTwo = userTwo;
    }

    public ChessGameType getChessGameType() {
        return chessGameType;
    }

    public void setChessGameType(ChessGameType chessGameType) {
        this.chessGameType = chessGameType;
    }

    public ChessGameStatus getChessGameStatus() {
        return chessGameStatus;
    }

    public void setChessGameStatus(ChessGameStatus chessGameStatus) {
        this.chessGameStatus = chessGameStatus;
    }

    public Color getNextMove() {
        return nextMove;
    }

    public void setNextMove(Color nextMove) {
        this.nextMove = nextMove;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public int getLastMoveId() {
        return lastMoveId;
    }

    public void setLastMoveId(int lastMoveId) {
        this.lastMoveId = lastMoveId;
    }

    public Set<FigureDto> getFigures() {
        return figures;
    }

    public void setFigures(Set<FigureDto> figures) {
        this.figures = figures;
    }

    public boolean isDrawOffered() {
        return drawOffered;
    }

    public void setDrawOffered(boolean drawOffered) {
        this.drawOffered = drawOffered;
    }

    public String getMoveHistory() {
        return moveHistory;
    }

    public void setMoveHistory(String moveHistory) {
        this.moveHistory = moveHistory;
    }
}
