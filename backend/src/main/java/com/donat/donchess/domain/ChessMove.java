package com.donat.donchess.domain;

import com.donat.donchess.domain.enums.SpecialMoveType;
import com.donat.donchess.model.enums.ChessFigure;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Entity
@Table(name = "chess_moves")
public class ChessMove {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private ChessGame chessGame;

    private int moveId;

    @Min(value = 1)
    @Max(value = 8)
    private Integer moveFromX;

    @Min(value = 1)
    @Max(value = 8)
    private Integer moveFromY;

    @Min(value = 1)
    @Max(value = 8)
    private Integer moveToX;

    @Min(value = 1)
    @Max(value = 8)
    private Integer moveToY;

    @Enumerated(EnumType.STRING)
    private SpecialMoveType specialMoveType;

    @Enumerated(EnumType.STRING)
    private ChessFigure promoteType;

    private Boolean chessGiven = false;

    private Boolean drawOffered = false;

    public ChessGame getChessGame() {
        return chessGame;
    }

    public void setChessGame(ChessGame chessGame) {
        this.chessGame = chessGame;
    }

    public Boolean isChessGiven() {
        return chessGiven;
    }

    public void setChessGiven(Boolean chessGiven) {
        this.chessGiven = chessGiven;
    }

    public ChessFigure getPromoteType() {
        return promoteType;
    }

    public void setPromoteType(ChessFigure promoteType) {
        this.promoteType = promoteType;
    }

    public SpecialMoveType getSpecialMoveType() {
        return specialMoveType;
    }

    public void setSpecialMoveType(SpecialMoveType specialMoveType) {
        this.specialMoveType = specialMoveType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getMoveId() {
        return moveId;
    }

    public void setMoveId(int moveId) {
        this.moveId = moveId;
    }

    public Integer getMoveFromX() {
        return moveFromX;
    }

    public void setMoveFromX(Integer moveFromX) {
        this.moveFromX = moveFromX;
    }

    public Integer getMoveFromY() {
        return moveFromY;
    }

    public void setMoveFromY(Integer moveFromY) {
        this.moveFromY = moveFromY;
    }

    public Integer getMoveToX() {
        return moveToX;
    }

    public void setMoveToX(Integer moveToX) {
        this.moveToX = moveToX;
    }

    public Integer getMoveToY() {
        return moveToY;
    }

    public void setMoveToY(Integer moveToY) {
        this.moveToY = moveToY;
    }

    public Boolean getChessGiven() {
        return chessGiven;
    }

    public Boolean getDrawOffered() {
        return drawOffered;
    }

    public void setDrawOffered(Boolean drawOffered) {
        this.drawOffered = drawOffered;
    }
}
