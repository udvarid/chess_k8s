package com.donat.donchess.dto.chessGame;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ValidMovesDto {

    private Long chessGameId;

    private List<CoordinateDto> validMoves = new ArrayList<>();

    public Long getChessGameId() {
        return chessGameId;
    }

    public void setChessGameId(Long chessGameId) {
        this.chessGameId = chessGameId;
    }

    public List<CoordinateDto> getValidMoves() {
        return validMoves;
    }

    public void setValidMoves(List<CoordinateDto> validMoves) {
        this.validMoves = validMoves;
    }
}
