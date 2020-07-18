package com.donat.donchess.model.logic;

import com.donat.donchess.exceptions.NotFoundException;
import com.donat.donchess.model.enums.ChessFigure;
import com.donat.donchess.model.objects.ChessTable;
import com.donat.donchess.model.objects.Coordinate;
import com.donat.donchess.model.objects.Figure;
import com.donat.donchess.model.objects.ValidMove;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class ChessJudge {

    private ValidMoveInspector validMoveInspector;

    public ChessJudge(ValidMoveInspector validMoveInspector) {
        this.validMoveInspector = validMoveInspector;
    }

    public boolean inChessSituation(ChessTable chessTableForCheck) {

        Figure king = findKing(chessTableForCheck);

        for (Figure figure : chessTableForCheck.getFigures()) {
            if (figure.getColor().equals(chessTableForCheck.getWhoIsNext())) {
                Set<ValidMove> validMoves = validMoveInspector.allValidMoves(chessTableForCheck,
                        new Coordinate(figure.getCoordX(), figure.getCoordY()));

                for (ValidMove validMove : validMoves) {
                    if (validMove.getCoordinate().equals(new Coordinate(king.getCoordX(), king.getCoordY()))) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private Figure findKing(ChessTable chessTableForCheck) {
        return chessTableForCheck
                .getFigures()
                .stream()
                .filter(figure ->
                        figure.getFigureType().equals(ChessFigure.KING) &&
                                !figure.getColor().equals(chessTableForCheck.getWhoIsNext())
                )
                .findAny()
                .orElseThrow(() -> new NotFoundException("King can not be found"));
    }

}
