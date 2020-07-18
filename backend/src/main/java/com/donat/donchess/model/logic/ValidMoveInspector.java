package com.donat.donchess.model.logic;

import com.donat.donchess.domain.enums.SpecialMoveType;
import com.donat.donchess.exceptions.NotFoundException;
import com.donat.donchess.model.enums.ChessFigure;
import com.donat.donchess.model.enums.Color;
import com.donat.donchess.model.objects.ChessTable;
import com.donat.donchess.model.objects.Coordinate;
import com.donat.donchess.model.objects.Figure;
import com.donat.donchess.model.objects.ValidMove;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class ValidMoveInspector {
    public Set<ValidMove> allValidMoves(ChessTable chessTable, Coordinate coordinateOfFigure) {
        Figure figureToMove = findFigure(chessTable.getFigures(), coordinateOfFigure);
        if (figureToMove == null) {
            throw new NotFoundException("The figure can not be found");
        }
        Set<ValidMove> validMoves = new HashSet<>();

        if (figureToMove.getFigureType().equals(ChessFigure.PAWN)) {
            fillValidMovesForPawn(validMoves, figureToMove, chessTable);
        } else if (figureToMove.getFigureType().equals(ChessFigure.ROOK)) {
            fillValidMovesForRook(validMoves, figureToMove, chessTable);
        } else if (figureToMove.getFigureType().equals(ChessFigure.BISHOP)) {
            fillValidMovesForBishop(validMoves, figureToMove, chessTable);
        } else if (figureToMove.getFigureType().equals(ChessFigure.KNIGHT)) {
            fillValidMovesForKnight(validMoves, figureToMove, chessTable);
        } else if (figureToMove.getFigureType().equals(ChessFigure.QUEEN)) {
            fillValidMovesForRook(validMoves, figureToMove, chessTable);
            fillValidMovesForBishop(validMoves, figureToMove, chessTable);
        } else if (figureToMove.getFigureType().equals(ChessFigure.KING)) {
            fillValidMovesForKing(validMoves, figureToMove, chessTable);
        }

        return validMoves;
    }

    private void fillValidMovesForKing(Set<ValidMove> validMoves, Figure figureToMove, ChessTable chessTable) {
        for (int i = -1; i <= +1; i++) {
            for (int j = -1; j <= +1; j++) {
                if (i != 0 || j != 0) {
                    Coordinate coordinate =
                            new Coordinate(figureToMove.getCoordX() + i, figureToMove.getCoordY() + j);
                    validGoalAndAdd(validMoves, coordinate, figureToMove, chessTable.getFigures());
                }
            }
        }

        if (!figureToMove.isMoved()) {
            validateCastlingToRight(validMoves, figureToMove, chessTable);
            validateCastlingToLeft(validMoves, figureToMove, chessTable);
        }

    }

    private void validateCastlingToRight(Set<ValidMove> validMoves, Figure figureToMove, ChessTable chessTable) {
        Figure rookToRight = findFigure(chessTable.getFigures(),
                new Coordinate(8, figureToMove.getCoordY()));
        if (rookToRight != null && !rookToRight.isMoved()) {
            Figure cellIsEmpty1 = findFigure(chessTable.getFigures(),
                    new Coordinate(6, figureToMove.getCoordY()));
            Figure cellIsEmpty2 = findFigure(chessTable.getFigures(),
                    new Coordinate(7, figureToMove.getCoordY()));
            if (cellIsEmpty1 == null && cellIsEmpty2 == null) {
                validMoves.add(new ValidMove(new Coordinate(7, figureToMove.getCoordY()),
                        SpecialMoveType.CASTLING));
            }
        }
    }

    private void validateCastlingToLeft(Set<ValidMove> validMoves, Figure figureToMove, ChessTable chessTable) {
        Figure rookToLeft = findFigure(chessTable.getFigures(),
                new Coordinate(1, figureToMove.getCoordY()));
        if (rookToLeft != null && !rookToLeft.isMoved()) {
            Figure cellIsEmpty1 = findFigure(chessTable.getFigures(),
                    new Coordinate(2, figureToMove.getCoordY()));
            Figure cellIsEmpty2 = findFigure(chessTable.getFigures(),
                    new Coordinate(3, figureToMove.getCoordY()));
            Figure cellIsEmpty3 = findFigure(chessTable.getFigures(),
                    new Coordinate(4, figureToMove.getCoordY()));
            if (cellIsEmpty1 == null && cellIsEmpty2 == null && cellIsEmpty3 == null) {
                validMoves.add(new ValidMove(new Coordinate(3, figureToMove.getCoordY()),
                        SpecialMoveType.CASTLING));
            }
        }
    }


    private void fillValidMovesForKnight(Set<ValidMove> validMoves, Figure figureToMove, ChessTable chessTable) {

        int jump = 1;
        int mirrorLeft = 1;
        int mirrorRight = 1;
        for (int i = 1; i <= 8; i++) {
            Coordinate knightMove = new Coordinate(figureToMove.getCoordX() + jump * mirrorLeft,
                    figureToMove.getCoordY() + (3 - jump) * mirrorRight);
            validGoalAndAdd(validMoves, knightMove, figureToMove, chessTable.getFigures());

            if (jump == 2) {
                jump = 1;
                mirrorLeft *= -1;
                if (i != 4) {
                    mirrorRight *= -1;
                }
            } else {
                jump = 2;
            }
        }
    }

    private void fillValidMovesForBishop(Set<ValidMove> validMoves, Figure figureToMove, ChessTable chessTable) {
        //fel-bal
        for (int i = 1; i <= 7; i++) {
            Coordinate move = new Coordinate(figureToMove.getCoordX() - i, figureToMove.getCoordY() + i);
            if (!validGoalAndAdd(validMoves, move, figureToMove, chessTable.getFigures())) {
                break;
            }
        }
        //fel-jobb
        for (int i = 1; i <= 7; i++) {
            Coordinate move = new Coordinate(figureToMove.getCoordX() + i, figureToMove.getCoordY() + i);
            if (!validGoalAndAdd(validMoves, move, figureToMove, chessTable.getFigures())) {
                break;
            }
        }
        //le-bal
        for (int i = 1; i <= 7; i++) {
            Coordinate move = new Coordinate(figureToMove.getCoordX() - i, figureToMove.getCoordY() - i);
            if (!validGoalAndAdd(validMoves, move, figureToMove, chessTable.getFigures())) {
                break;
            }
        }
        //le-jobb
        for (int i = 1; i <= 7; i++) {
            Coordinate move = new Coordinate(figureToMove.getCoordX() + i, figureToMove.getCoordY() - i);
            if (!validGoalAndAdd(validMoves, move, figureToMove, chessTable.getFigures())) {
                break;
            }
        }
    }

    private void fillValidMovesForRook(Set<ValidMove> validMoves, Figure figureToMove, ChessTable chessTable) {
        //felfelé
        for (int i = 1; i <= 7; i++) {
            Coordinate move = new Coordinate(figureToMove.getCoordX(), figureToMove.getCoordY() + i);
            if (!validGoalAndAdd(validMoves, move, figureToMove, chessTable.getFigures())) {
                break;
            }
        }
        //lefelé
        for (int i = 1; i <= 7; i++) {
            Coordinate move = new Coordinate(figureToMove.getCoordX(), figureToMove.getCoordY() - i);
            if (!validGoalAndAdd(validMoves, move, figureToMove, chessTable.getFigures())) {
                break;
            }
        }
        //balra
        for (int i = 1; i <= 7; i++) {
            Coordinate move = new Coordinate(figureToMove.getCoordX() - i, figureToMove.getCoordY());
            if (!validGoalAndAdd(validMoves, move, figureToMove, chessTable.getFigures())) {
                break;
            }
        }
        //jobbra
        for (int i = 1; i <= 7; i++) {
            Coordinate move = new Coordinate(figureToMove.getCoordX() + i, figureToMove.getCoordY());
            if (!validGoalAndAdd(validMoves, move, figureToMove, chessTable.getFigures())) {
                break;
            }
        }

    }

    private void fillValidMovesForPawn(Set<ValidMove> validMoves, Figure figureToMove, ChessTable chessTable) {
        int mirror = figureToMove.getColor().equals(Color.WHITE) ? 1 : -1;
        Figure figureToCheck = null;
        Figure figureToCheckTwoStepsAhead = null;

        Coordinate oneStepfw = new Coordinate(figureToMove.getCoordX(), figureToMove.getCoordY() + 1 * mirror);
        figureToCheck = findFigure(chessTable.getFigures(), oneStepfw);
        validGoalForPawnMovingAndAdd(validMoves, oneStepfw, figureToCheck);

        Coordinate twoStepfw = new Coordinate(figureToMove.getCoordX(), figureToMove.getCoordY() + 2 * mirror);
        figureToCheckTwoStepsAhead = findFigure(chessTable.getFigures(), twoStepfw);
        if (!figureToMove.isMoved()) {
            validGoalForPawnMovingAndAddDouple(validMoves, twoStepfw, figureToCheck, figureToCheckTwoStepsAhead);
        }

        Coordinate attackLeft = new Coordinate(figureToMove.getCoordX() - 1, figureToMove.getCoordY() + 1 * mirror);
        Coordinate attackLeftEnPassan =
                new Coordinate(figureToMove.getCoordX() - 1, figureToMove.getCoordY());
        pawnHitting(validMoves, figureToMove, chessTable, attackLeft, attackLeftEnPassan);


        Coordinate attackRight = new Coordinate(figureToMove.getCoordX() + 1, figureToMove.getCoordY() + 1 * mirror);
        Coordinate attackRightEnPassan =
                new Coordinate(figureToMove.getCoordX() + 1, figureToMove.getCoordY());
        pawnHitting(validMoves, figureToMove, chessTable, attackRight, attackRightEnPassan);

    }

    private void pawnHitting(Set<ValidMove> validMoves, Figure figureToMove, ChessTable chessTable, Coordinate attack, Coordinate attackEnPassan) {
        Figure figureToCheck;
        Figure enPassanFigureToCheck;
        figureToCheck = findFigure(chessTable.getFigures(), attack);
        enPassanFigureToCheck = findFigure(chessTable.getFigures(), attackEnPassan);

        if (figureToCheck != null) {
            validGoalForPawnHittingAndAdd(validMoves, attack, figureToMove, figureToCheck);
        } else if (chessTable.isLastMoveWasDoublePawn() &&
                enPassanFigureToCheck != null &&
                enPassanFigureToCheck.getFigureType().equals(ChessFigure.PAWN) &&
                !figureToMove.getColor().equals(enPassanFigureToCheck.getColor()) &&
                (figureToMove.getColor().equals(Color.WHITE) ? 5 : 4) == figureToMove.getCoordY() &&
                chessTable.getColumnIndexIfLastMoveWasDoublePawn() == enPassanFigureToCheck.getCoordX() &&
                validCoordinate(attack)) {
            validMoves.add(new ValidMove(attack, SpecialMoveType.EN_PASSAN));
        }
    }

    private void validGoalForPawnMovingAndAdd(Set<ValidMove> validMoves, Coordinate aimCoord, Figure figureToCheck) {
        if (validCoordinate(aimCoord) && figureToCheck == null) {
            validMoves.add(new ValidMove(aimCoord, SpecialMoveType.NORMAL));
        }
    }

    private void validGoalForPawnMovingAndAddDouple(Set<ValidMove> validMoves, Coordinate aimCoord, Figure figureToCheck, Figure figureToCheckTwoStepsAhead) {
        if (validCoordinate(aimCoord) && figureToCheck == null && figureToCheckTwoStepsAhead == null) {
            validMoves.add(new ValidMove(aimCoord, SpecialMoveType.NORMAL));
        }
    }

    private void validGoalForPawnHittingAndAdd(Set<ValidMove> validMoves, Coordinate aimCoord, Figure figureToMove, Figure figureToCheck) {
        if (validCoordinate(aimCoord) &&
                figureToCheck != null &&
                !figureToCheck.getColor().equals(figureToMove.getColor())) {
            validMoves.add(new ValidMove(aimCoord, SpecialMoveType.NORMAL));
        }
    }


    private boolean validGoalAndAdd(Set<ValidMove> validMoves, Coordinate aimCoord,
                                    Figure figureToMove,
                                    Set<Figure> figures) {
        Figure figureToCheck = findFigure(figures, aimCoord);
        if (validCoordinate(aimCoord) &&
                (figureToCheck == null || !figureToCheck.getColor().equals(figureToMove.getColor()))) {

            validMoves.add(new ValidMove(aimCoord, SpecialMoveType.NORMAL));
            //ha üthető egy ellenséges bábú, az utána lévő cellákra már nem léphet
            return figureToCheck == null;
        }
        return false;
    }

    private boolean validCoordinate(Coordinate aimCoord) {
        return aimCoord.getY() >= 1 &&
                aimCoord.getY() <= 8 &&
                aimCoord.getX() >= 1 &&
                aimCoord.getX() <= 8;
    }


    public Figure findFigure(Set<Figure> figures, Coordinate coordinateOfFigure) {
        return figures
                .stream()
                .filter(figure -> figure.getCoordX() == coordinateOfFigure.getX() &&
                        figure.getCoordY() == coordinateOfFigure.getY())
                .findAny()
                .orElse(null);
    }
}
