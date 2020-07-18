package com.donat.donchess.domain;

import com.donat.donchess.domain.enums.ChessGameStatus;
import com.donat.donchess.domain.enums.ChessGameType;
import com.donat.donchess.domain.enums.Result;
import com.donat.donchess.model.enums.Color;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "chess_games")
public class ChessGame {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @NotNull
    private User userOne;

    @ManyToOne
    @NotNull
    private User userTwo;

    @Enumerated(EnumType.STRING)
    private ChessGameType chessGameType;

    @Enumerated(EnumType.STRING)
    private ChessGameStatus chessGameStatus;

    @Enumerated(EnumType.STRING)
    private Color nextMove;

    @Enumerated(EnumType.STRING)
    private Result result;

    private int lastMoveId;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ChessMove> chessMoves = new ArrayList<>();

    private LocalDateTime creationTime;

    private LocalDateTime finishTime;

    //TODO később bevezetni az időperiódust és számon tartani, az egyes felhasználók rendelkezésre álló idejét


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUserOne() {
        return userOne;
    }

    public void setUserOne(User userOne) {
        this.userOne = userOne;
    }

    public User getUserTwo() {
        return userTwo;
    }

    public void setUserTwo(User userTwo) {
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

    public List<ChessMove> getChessMoves() {
        return chessMoves;
    }

    public void setChessMoves(List<ChessMove> chessMoves) {
        this.chessMoves = chessMoves;
    }

    public Color getNextMove() {
        return nextMove;
    }

    public void setNextMove(Color nextMove) {
        this.nextMove = nextMove;
    }

    public LocalDateTime getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(LocalDateTime creationTime) {
        this.creationTime = creationTime;
    }

    public LocalDateTime getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(LocalDateTime finishTime) {
        this.finishTime = finishTime;
    }
}
