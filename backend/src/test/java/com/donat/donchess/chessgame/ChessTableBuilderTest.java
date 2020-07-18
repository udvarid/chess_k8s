package com.donat.donchess.chessgame;

import com.donat.donchess.AncestorAbstract;
import com.donat.donchess.domain.ChessGame;
import com.donat.donchess.domain.ChessMove;
import com.donat.donchess.domain.enums.ChessGameType;
import com.donat.donchess.domain.enums.SpecialMoveType;
import com.donat.donchess.model.objects.ChessTable;
import com.donat.donchess.model.objects.Figure;
import com.donat.donchess.model.enums.ChessFigure;
import com.donat.donchess.model.enums.Color;
import com.donat.donchess.service.TableBuilderService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

import static org.junit.Assert.*;

public class ChessTableBuilderTest extends AncestorAbstract {

    @Autowired
    TableBuilderService tableBuilderService;

    @Test
    public void initSetIsOkTest() {
        ChessTable chessTable = new ChessTable();
        ChessGame chessGame = new ChessGame();
        chessGame.setChessGameType(ChessGameType.NORMAL);
        tableBuilderService.initChessTable(chessTable, chessGame);

        assertEquals(32, chessTable.getFigures().size());
        for (Figure figure : chessTable.getFigures()) {
            assertTrue(singleCoordinate(figure, chessTable.getFigures()));
        }

    }

    private boolean singleCoordinate(Figure figure, Set<Figure> figures) {

        for (Figure otherFigure : figures) {
            if (!otherFigure.equals(figure) &&
                    otherFigure.getCoordY() == figure.getCoordY() &&
                    otherFigure.getCoordX() == figure.getCoordX()) {
                return false;
            }
        }
        return true;
    }


    @Test
    public void findFigureTest() {
        ChessTable chessTable = new ChessTable();
        ChessGame chessGame = new ChessGame();
        chessGame.setChessGameType(ChessGameType.NORMAL);
        tableBuilderService.initChessTable(chessTable, chessGame);

        Figure figureTofound1 = tableBuilderService.findFigure(chessTable.getFigures(), 5, 2);
        Figure figureTofound2 = tableBuilderService.findFigure(chessTable.getFigures(), 2, 1);
        Figure figureTofound3 = tableBuilderService.findFigure(chessTable.getFigures(), 5, 1);
        Figure figureTofound4 = tableBuilderService.findFigure(chessTable.getFigures(), 8, 8);

        assertTrue(figureTofound1.getFigureType().equals(ChessFigure.PAWN));
        assertTrue(figureTofound1.getCoordX() == 5);
        assertTrue(figureTofound2.getFigureType().equals(ChessFigure.KNIGHT));
        assertTrue(figureTofound2.getColor().equals(Color.WHITE));
        assertTrue(figureTofound3.getFigureType().equals(ChessFigure.KING));
        assertTrue(figureTofound3.getCoordY() == 1);
        assertTrue(figureTofound4.getFigureType().equals(ChessFigure.ROOK));
        assertTrue(figureTofound4.getColor().equals(Color.BLACK));

    }

    @Test
    public void normalMoveTest() {
        ChessTable chessTable = new ChessTable();
        ChessGame chessGame = new ChessGame();
        chessGame.setChessGameType(ChessGameType.NORMAL);
        tableBuilderService.initChessTable(chessTable, chessGame);

        ChessMove chessMove = new ChessMove();
        chessMove.setId(1l);
        chessMove.setMoveId(1);
        chessMove.setSpecialMoveType(SpecialMoveType.NORMAL);
        chessMove.setMoveFromX(2);
        chessMove.setMoveFromY(1);
        chessMove.setMoveToX(3);
        chessMove.setMoveToY(3);

        assertNull(tableBuilderService
                .findFigure(chessTable.getFigures(), chessMove.getMoveToX(), chessMove.getMoveToY()));

        tableBuilderService.makeMove(chessTable, chessMove);

        Figure movedFigure = tableBuilderService
                .findFigure(chessTable.getFigures(), chessMove.getMoveToX(), chessMove.getMoveToY());

        assertNotNull(movedFigure);
        assertTrue(movedFigure.getCoordX() == chessMove.getMoveToX());
        assertTrue(movedFigure.getCoordY() == chessMove.getMoveToY());
        assertTrue(movedFigure.isMoved());


    }

    @Test
    public void registerPawnMoveTest() {
        ChessTable chessTable = new ChessTable();
        ChessGame chessGame = new ChessGame();
        chessGame.setChessGameType(ChessGameType.NORMAL);
        tableBuilderService.initChessTable(chessTable, chessGame);

        ChessMove chessMove = new ChessMove();
        chessMove.setId(1l);
        chessMove.setMoveId(1);
        chessMove.setSpecialMoveType(SpecialMoveType.NORMAL);
        chessMove.setMoveFromX(5);
        chessMove.setMoveFromY(2);
        chessMove.setMoveToX(5);
        chessMove.setMoveToY(3);

        Figure pawnFigure = tableBuilderService
                .findFigure(chessTable.getFigures(), chessMove.getMoveFromX(), chessMove.getMoveFromY());

        assertTrue(pawnFigure.getFigureType().equals(ChessFigure.PAWN));

        tableBuilderService.makeMove(chessTable, chessMove);

        assertTrue(pawnFigure.getCoordX() == chessMove.getMoveToX());
        assertTrue(pawnFigure.getCoordY() == chessMove.getMoveToY());
        assertTrue(pawnFigure.isMoved());
        assertTrue(chessTable.getLastPawnMoveNumber() == chessMove.getMoveId());

    }

    @Test
    public void registerDoublePawnMoveTest() {
        ChessTable chessTable = new ChessTable();
        ChessGame chessGame = new ChessGame();
        chessGame.setChessGameType(ChessGameType.NORMAL);
        tableBuilderService.initChessTable(chessTable, chessGame);

        ChessMove chessMove = new ChessMove();
        chessMove.setId(1l);
        chessMove.setMoveId(1);
        chessMove.setSpecialMoveType(SpecialMoveType.NORMAL);
        chessMove.setMoveFromX(5);
        chessMove.setMoveFromY(2);
        chessMove.setMoveToX(5);
        chessMove.setMoveToY(4);

        Figure pawnFigure = tableBuilderService
                .findFigure(chessTable.getFigures(), chessMove.getMoveFromX(), chessMove.getMoveFromY());

        assertTrue(pawnFigure.getFigureType().equals(ChessFigure.PAWN));

        tableBuilderService.makeMove(chessTable, chessMove);

        assertTrue(pawnFigure.getCoordX() == chessMove.getMoveToX());
        assertTrue(pawnFigure.getCoordY() == chessMove.getMoveToY());
        assertTrue(pawnFigure.isMoved());
        assertTrue(chessTable.getLastPawnMoveNumber() == chessMove.getMoveId());
        assertTrue(chessTable.isLastMoveWasDoublePawn());
        assertTrue(chessTable.getColumnIndexIfLastMoveWasDoublePawn() == chessMove.getMoveToX());

    }


    @Test
    public void moveWithHittingTest() {
        ChessTable chessTable = new ChessTable();
        ChessGame chessGame = new ChessGame();
        chessGame.setChessGameType(ChessGameType.NORMAL);
        tableBuilderService.initChessTable(chessTable, chessGame);

        ChessMove chessMove = new ChessMove();
        chessMove.setId(1l);
        chessMove.setMoveId(1);
        chessMove.setSpecialMoveType(SpecialMoveType.NORMAL);
        chessMove.setMoveFromX(5);
        chessMove.setMoveFromY(2);
        chessMove.setMoveToX(5);
        chessMove.setMoveToY(7);

        assertEquals(32, chessTable.getFigures().size());
        Figure figureToMove = tableBuilderService
                .findFigure(chessTable.getFigures(), chessMove.getMoveFromX(), chessMove.getMoveFromY());
        Figure figureToKill = tableBuilderService
                .findFigure(chessTable.getFigures(), chessMove.getMoveToX(), chessMove.getMoveToY());
        assertNotNull(figureToMove);
        assertNotNull(figureToKill);

        tableBuilderService.makeMove(chessTable, chessMove);

        assertEquals(31, chessTable.getFigures().size());
        assertTrue(chessTable.getLastHitMoveNumber() == chessMove.getMoveId());
        assertFalse(chessTable.getFigures().contains(figureToKill));


    }

    @Test
    public void promotingTest() {
        ChessTable chessTable = new ChessTable();
        ChessGame chessGame = new ChessGame();
        chessGame.setChessGameType(ChessGameType.NORMAL);
        tableBuilderService.initChessTable(chessTable, chessGame);

        Figure figureToDeleteFromWay = tableBuilderService.findFigure(chessTable.getFigures(),1,8);
        chessTable.getFigures().remove(figureToDeleteFromWay);

        ChessMove chessMove = new ChessMove();
        chessMove.setId(1l);
        chessMove.setMoveId(1);
        chessMove.setSpecialMoveType(SpecialMoveType.NORMAL);
        chessMove.setPromoteType(ChessFigure.QUEEN);
        chessMove.setMoveFromX(1);
        chessMove.setMoveFromY(2);
        chessMove.setMoveToX(1);
        chessMove.setMoveToY(8);

        Figure figureToPromote = tableBuilderService.findFigure(chessTable.getFigures(),
                chessMove.getMoveFromX(), chessMove.getMoveFromY());

        assertTrue(figureToPromote.getFigureType().equals(ChessFigure.PAWN));

        tableBuilderService.makeMove(chessTable, chessMove);

        assertTrue(figureToPromote.getCoordY() == 8);
        assertTrue(figureToPromote.getFigureType().equals(ChessFigure.QUEEN));
    }

    @Test
    public void enPassanTest() {
        ChessTable chessTable = new ChessTable();
        ChessGame chessGame = new ChessGame();
        chessGame.setChessGameType(ChessGameType.NORMAL);
        tableBuilderService.initChessTable(chessTable, chessGame);

        Figure figureToKillWithEnPassan =
                tableBuilderService.findFigure(chessTable.getFigures(),
                        5, 2);
        Figure figureMakeEnPassan =
                tableBuilderService.findFigure(chessTable.getFigures(),
                        4,7);

        figureToKillWithEnPassan.setCoordY(4);
        figureMakeEnPassan.setCoordY(4);

        ChessMove chessMove = new ChessMove();
        chessMove.setId(1l);
        chessMove.setMoveId(1);
        chessMove.setSpecialMoveType(SpecialMoveType.NORMAL);
        chessMove.setMoveFromX(4);
        chessMove.setMoveFromY(4);
        chessMove.setMoveToX(5);
        chessMove.setMoveToY(3);

        tableBuilderService.makeMove(chessTable,chessMove);
        assertEquals(32, chessTable.getFigures().size());

        figureMakeEnPassan.setCoordX(4);
        figureMakeEnPassan.setCoordY(4);
        chessMove.setSpecialMoveType(SpecialMoveType.EN_PASSAN);
        tableBuilderService.makeMove(chessTable,chessMove);
        assertEquals(31, chessTable.getFigures().size());
        assertTrue(!chessTable.getFigures().contains(figureToKillWithEnPassan));
    }

    @Test
    public void shortCastlingTest() {
        ChessTable chessTable = new ChessTable();
        ChessGame chessGame = new ChessGame();
        chessGame.setChessGameType(ChessGameType.NORMAL);
        tableBuilderService.initChessTable(chessTable, chessGame);

        Figure knightToRemoveFromway =
                tableBuilderService.findFigure(chessTable.getFigures(),
                        7,1);
        Figure bishopToRemoveFromway =
                tableBuilderService.findFigure(chessTable.getFigures(),
                        6,1);

        chessTable.getFigures().remove(knightToRemoveFromway);
        chessTable.getFigures().remove(bishopToRemoveFromway);

        ChessMove chessMove = new ChessMove();
        chessMove.setId(1l);
        chessMove.setMoveId(1);
        chessMove.setSpecialMoveType(SpecialMoveType.CASTLING);
        chessMove.setMoveFromX(5);
        chessMove.setMoveFromY(1);
        chessMove.setMoveToX(7);
        chessMove.setMoveToY(1);

        Figure rookToMoveWithCastling =
                tableBuilderService.findFigure(chessTable.getFigures(),
                        8,1);

        tableBuilderService.makeMove(chessTable,chessMove);
        assertTrue(rookToMoveWithCastling.getCoordX() == 6);
    }

    @Test
    public void longCastlingTest() {
        ChessTable chessTable = new ChessTable();
        ChessGame chessGame = new ChessGame();
        chessGame.setChessGameType(ChessGameType.NORMAL);
        tableBuilderService.initChessTable(chessTable, chessGame);

        Figure knightToRemoveFromway =
                tableBuilderService.findFigure(chessTable.getFigures(),
                        2,1);
        Figure bishopToRemoveFromway =
                tableBuilderService.findFigure(chessTable.getFigures(),
                        3,1);

        Figure queenToRemoveFromway =
                tableBuilderService.findFigure(chessTable.getFigures(),
                        4,1);

        chessTable.getFigures().remove(knightToRemoveFromway);
        chessTable.getFigures().remove(bishopToRemoveFromway);
        chessTable.getFigures().remove(queenToRemoveFromway);

        ChessMove chessMove = new ChessMove();
        chessMove.setId(1l);
        chessMove.setMoveId(1);
        chessMove.setSpecialMoveType(SpecialMoveType.CASTLING);
        chessMove.setMoveFromX(5);
        chessMove.setMoveFromY(1);
        chessMove.setMoveToX(3);
        chessMove.setMoveToY(1);

        Figure rookToMoveWithCastling =
                tableBuilderService.findFigure(chessTable.getFigures(),
                        1,1);

        tableBuilderService.makeMove(chessTable,chessMove);
        assertTrue(rookToMoveWithCastling.getCoordX() == 4);
    }

}

