package com.donat.donchess.model.logic;

import com.donat.donchess.domain.enums.SpecialMoveType;
import com.donat.donchess.dto.chessGame.ChessMoveDto;
import com.donat.donchess.model.enums.ChessFigure;
import com.donat.donchess.model.enums.Color;
import com.donat.donchess.model.objects.ChessTable;
import com.donat.donchess.model.objects.Coordinate;
import com.donat.donchess.model.objects.Figure;
import com.donat.donchess.model.objects.ValidMove;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class MoveValidator {

    private ChessJudge chessJudge;
    private ValidMoveInspector validMoveInspector;

    public MoveValidator(ChessJudge chessJudge,
                         ValidMoveInspector validMoveInspector) {
        this.chessJudge = chessJudge;
        this.validMoveInspector = validMoveInspector;
    }

    public ValidMove validmove(ChessTable chessTable, ChessMoveDto chessMoveDto) {

        Coordinate coordinateOfFigure = new Coordinate(chessMoveDto.getMoveFromX(), chessMoveDto.getMoveFromY());
        Coordinate aimCoordinate = new Coordinate(chessMoveDto.getMoveToX(), chessMoveDto.getMoveToY());

        Set<ValidMove> validMoves = validMoveInspector.allValidMoves(chessTable, coordinateOfFigure);

        ValidMove validMove = validMoves
                .stream()
                .filter(vm -> vm.getCoordinate().equals(aimCoordinate))
                .findFirst()
                .orElse(null);

        boolean answer = false;
        if (validMove != null) {
            if (!chessJudge.inChessSituation(cloneTableAndMakeMove(chessTable, chessMoveDto))) {
                answer = true;
            }
            if (validMove.getSpecialMoveType().equals(SpecialMoveType.CASTLING)) {
                ChessMoveDto modifiedChessMoveDto = makeMoveDtoToFullyValidateCastlingMove(chessMoveDto);
                if (chessJudge.inChessSituation(cloneTableAndMakeMove(chessTable, modifiedChessMoveDto))) {
                    answer = false;
                }
            }
            if (pawnIsMovingToLastLine(chessTable, chessMoveDto) &&
                    noPromotingIsSet(chessMoveDto)) {
                chessMoveDto.setPromoteToFigure("QUEEN");
            }

        }

        return answer ? validMove : null;

    }

    private boolean noPromotingIsSet(ChessMoveDto chessMoveDto) {
        return chessMoveDto.getPromoteToFigure() == null || chessMoveDto.getPromoteToFigure().isEmpty();
    }

    private boolean pawnIsMovingToLastLine(ChessTable chessTable, ChessMoveDto chessMoveDto) {
        Figure figure = getFigure(chessTable, chessMoveDto);
        if (figure != null && figure.getFigureType().equals(ChessFigure.PAWN)) {
            if (figure.getColor().equals(Color.WHITE) && chessMoveDto.getMoveToY() == 8 ||
                    figure.getColor().equals(Color.BLACK) && chessMoveDto.getMoveToY() == 1) {
                return true;
            }
        }
        return false;
    }

    private Figure getFigure(ChessTable chessTable, ChessMoveDto chessMoveDto) {
        for (Figure figure : chessTable.getFigures()) {
            if (figure.getCoordX() == chessMoveDto.getMoveFromX() &&
                    figure.getCoordY() == chessMoveDto.getMoveFromY()) {
                return figure;
            }
        }
        return null;
    }


    private ChessMoveDto makeMoveDtoToFullyValidateCastlingMove(ChessMoveDto chessMoveDto) {
        ChessMoveDto modifiedChessMoveDto = new ChessMoveDto();
        modifiedChessMoveDto.setGameId(chessMoveDto.getGameId());
        modifiedChessMoveDto.setMoveId(chessMoveDto.getMoveId());
        modifiedChessMoveDto.setMoveFromX(chessMoveDto.getMoveFromX());
        modifiedChessMoveDto.setMoveFromY(chessMoveDto.getMoveFromY());
        modifiedChessMoveDto.setMoveToY(chessMoveDto.getMoveToY());
        modifiedChessMoveDto.setMoveToX(chessMoveDto.getMoveFromX() +
                (chessMoveDto.getMoveToX() - chessMoveDto.getMoveFromX()) / 2);
        return modifiedChessMoveDto;
    }

    public ChessTable cloneTableAndMakeMove(ChessTable chessTable, ChessMoveDto chessMoveDto) {

        ChessTable cloneChessTable = new ChessTable();

        for (Figure figure : chessTable.getFigures()) {
            Figure cloneFigure = new Figure(figure.getFigureType(), figure.getColor(),
                    figure.getCoordX(), figure.getCoordY());
            cloneFigure.setMoved(figure.isMoved());
            cloneChessTable.getFigures().add(cloneFigure);
        }

        cloneChessTable
                .setWhoIsNext(chessTable.getWhoIsNext().equals(Color.WHITE) ? Color.BLACK : Color.WHITE);

        cloneChessTable.setChessGameId(chessTable.getChessGameId());


        Figure figureToMove = validMoveInspector.findFigure(cloneChessTable.getFigures(),
                new Coordinate(chessMoveDto.getMoveFromX(), chessMoveDto.getMoveFromY()));
        Figure figureToKill = validMoveInspector.findFigure(cloneChessTable.getFigures(),
                new Coordinate(chessMoveDto.getMoveToX(), chessMoveDto.getMoveToY()));

        if (figureToKill != null) {
            cloneChessTable.getFigures().remove(figureToKill);
        }
        figureToMove.setCoordX(chessMoveDto.getMoveToX());
        figureToMove.setCoordY(chessMoveDto.getMoveToY());
        figureToMove.setMoved(true);
        cloneChessTable.setLastMoveWasDoublePawn(false);

        if (figureToMove.getFigureType().equals(ChessFigure.PAWN) &&
                Math.abs(chessMoveDto.getMoveFromY() - chessMoveDto.getMoveToY()) == 2) {
            cloneChessTable.setColumnIndexIfLastMoveWasDoublePawn(chessMoveDto.getMoveFromX());
            cloneChessTable.setLastMoveWasDoublePawn(true);
        }

        return cloneChessTable;
    }

}
