package com.donat.donchess.dto.chessGame;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

public class ChessMoveDto {

    private Long gameId;

    private int moveId;

    @Min(1)
    @Max(8)
    private int moveFromX;

    @Min(1)
    @Max(8)
    private int moveFromY;

    @Min(1)
    @Max(8)
    private int moveToX;

    @Min(1)
    @Max(8)
    private int moveToY;

    private String promoteToFigure;

    private boolean drawOffered;

    public Long getGameId() {
        return gameId;
    }

    public void setGameId(Long gameId) {
        this.gameId = gameId;
    }

    public int getMoveId() {
        return moveId;
    }

    public void setMoveId(int moveId) {
        this.moveId = moveId;
    }

    public int getMoveFromX() {
        return moveFromX;
    }

    public void setMoveFromX(int moveFromX) {
        this.moveFromX = moveFromX;
    }

    public int getMoveFromY() {
        return moveFromY;
    }

    public void setMoveFromY(int moveFromY) {
        this.moveFromY = moveFromY;
    }

    public int getMoveToX() {
        return moveToX;
    }

    public void setMoveToX(int moveToX) {
        this.moveToX = moveToX;
    }

    public int getMoveToY() {
        return moveToY;
    }

    public void setMoveToY(int moveToY) {
        this.moveToY = moveToY;
    }

    public String getPromoteToFigure() {
        return promoteToFigure;
    }

    public void setPromoteToFigure(String promoteToFigure) {
        this.promoteToFigure = promoteToFigure;
    }

	public boolean isDrawOffered() {
		return drawOffered;
	}

	public void setDrawOffered(boolean drawOffered) {
		this.drawOffered = drawOffered;
	}
}
