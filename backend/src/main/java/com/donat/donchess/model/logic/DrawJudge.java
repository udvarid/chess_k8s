package com.donat.donchess.model.logic;

import com.donat.donchess.domain.ChessGame;
import com.donat.donchess.domain.ChessMove;
import com.donat.donchess.domain.QChessGame;
import com.donat.donchess.domain.QChessMove;
import com.donat.donchess.dto.chessGame.ChessMoveDto;
import com.donat.donchess.model.enums.ChessFigure;
import com.donat.donchess.model.enums.Color;
import com.donat.donchess.model.objects.ChessTable;
import com.donat.donchess.model.objects.Coordinate;
import com.donat.donchess.model.objects.Figure;
import com.donat.donchess.model.objects.ValidMove;
import com.donat.donchess.service.TableBuilderService;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.inject.Provider;
import javax.persistence.EntityManager;
import java.util.List;
import java.util.Set;

@Service
public class DrawJudge {

    @Autowired
    private Provider<EntityManager> entityManager;

    private MoveValidator moveValidator;
    private ChessJudge chessJudge;
    private TableBuilderService tableBuilderService;
    private ValidMoveInspector validMoveInspector;

    public DrawJudge(MoveValidator moveValidator,
                     ChessJudge chessJudge,
                     TableBuilderService tableBuilderService,
                     ValidMoveInspector validMoveInspector) {
        this.moveValidator = moveValidator;
        this.chessJudge = chessJudge;
        this.tableBuilderService = tableBuilderService;
        this.validMoveInspector = validMoveInspector;
    }

    public boolean checkDraw(ChessTable chessTable) {

        if (fiftyMoveRule(chessTable)) {
            return true;
        }

        if (inSufficientMaterials(chessTable)) {
            return true;
        }

        if (noPossibleMove(chessTable)) {
            return true;
        }

        if (threeFoldRepetition(chessTable)) {
            return true;
        }

        return false;
    }

    public boolean threeFoldRepetition(ChessTable chessTable) {
        JPAQueryFactory query = new JPAQueryFactory(entityManager);
        QChessMove chessMoveFromQ = QChessMove.chessMove;
        QChessGame chessGameFromQ = QChessGame.chessGame;

        List<ChessMove> chessMoves = query
                .selectFrom(chessMoveFromQ)
                .orderBy(chessMoveFromQ.moveId.asc())
                .where(chessMoveFromQ.chessGame.id.eq(chessTable.getChessGameId()))
                .fetch();

        ChessGame chessGame = query
                .selectFrom(chessGameFromQ)
                .where(chessGameFromQ.id.eq(chessTable.getChessGameId()))
                .fetchOne();

        ChessTable chessTableForCompare = new ChessTable();
        chessTableForCompare.setChessGameId(chessTable.getChessGameId());
        chessTableForCompare.setWhoIsNext(chessTable.getWhoIsNext());
        chessTableForCompare.setActualMoveNumber(chessTable.getActualMoveNumber());

        tableBuilderService.initChessTable(chessTableForCompare, chessGame);
        int numberOfSameTable = 0;
        for (ChessMove chessMove : chessMoves) {
            tableBuilderService.makeMove(chessTableForCompare, chessMove);
            if (sameTable(chessTableForCompare.getFigures(), chessTable.getFigures())) {
                numberOfSameTable++;
            }
            if (numberOfSameTable == 3) {
                return true;
            }
        }

        return false;
    }

    public boolean sameTable(Set<Figure> figuresOne, Set<Figure> figuresTwo) {

        for (Figure figure : figuresOne) {
            if (figuresTwo.stream()
                    .noneMatch(figureInSet -> figureInSet.equals(figure))) {
                return false;
            }
        }
        for (Figure figure : figuresTwo) {
            if (figuresOne.stream()
                    .noneMatch(figureInSet -> figureInSet.equals(figure))) {
                return false;
            }
        }
        return true;
    }

    public boolean noPossibleMove(ChessTable chessTable) {
        for (Figure figure : chessTable.getFigures()) {
            if (figure.getColor().equals(chessTable.getWhoIsNext())) {
                Set<ValidMove> validMoves =
                        validMoveInspector.allValidMoves(chessTable, new Coordinate(figure.getCoordX(), figure.getCoordY()));

                for (ValidMove validMove : validMoves) {
                    ChessMoveDto chessMoveDto = setMoveDto(chessTable, figure, validMove);
                    if (!chessJudge
                            .inChessSituation(moveValidator.cloneTableAndMakeMove(chessTable, chessMoveDto))) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private ChessMoveDto setMoveDto(ChessTable chessTable, Figure figure, ValidMove validMove) {
        ChessMoveDto chessMoveDto = new ChessMoveDto();
        chessMoveDto.setGameId(chessTable.getChessGameId());
        chessMoveDto.setMoveFromX(figure.getCoordX());
        chessMoveDto.setMoveFromY(figure.getCoordY());
        chessMoveDto.setMoveId(chessTable.getActualMoveNumber());
        chessMoveDto.setMoveToX(validMove.getCoordinate().getX());
        chessMoveDto.setMoveToY(validMove.getCoordinate().getY());
        return chessMoveDto;
    }

    public boolean inSufficientMaterials(ChessTable chessTable) {
        return maxTwoPiecesOfEachSide(chessTable) &&
                onlyKingOrBishopOrKnightFiguresCanBeFound(chessTable);
    }

    private boolean onlyKingOrBishopOrKnightFiguresCanBeFound(ChessTable chessTable) {
        for (Figure figure : chessTable.getFigures()) {
            if (!figure.getFigureType().equals(ChessFigure.KING) &&
                    !figure.getFigureType().equals(ChessFigure.BISHOP) &&
                    !figure.getFigureType().equals(ChessFigure.KNIGHT)) {
                return false;
            }
        }


        return true;
    }

    private boolean maxTwoPiecesOfEachSide(ChessTable chessTable) {
        int numberOfWhite = 0;
        int numberOfBlack = 0;
        for (Figure figure : chessTable.getFigures()) {
            if (figure.getColor().equals(Color.WHITE)) {
                numberOfWhite++;
            } else {
                numberOfBlack++;
            }
            if (numberOfBlack > 2 || numberOfWhite > 2) {
                return false;
            }
        }
        return true;
    }

    public boolean fiftyMoveRule(ChessTable chessTable) {
        return chessTable.getActualMoveNumber() - chessTable.getLastPawnMoveNumber() >= 50 &&
                chessTable.getActualMoveNumber() - chessTable.getLastHitMoveNumber() >= 50;
    }
}
