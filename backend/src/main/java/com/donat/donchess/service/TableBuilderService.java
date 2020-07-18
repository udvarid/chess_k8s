package com.donat.donchess.service;

import com.donat.donchess.domain.ChessGame;
import com.donat.donchess.domain.ChessMove;
import com.donat.donchess.domain.QChessGame;
import com.donat.donchess.domain.QChessMove;
import com.donat.donchess.domain.enums.ChessGameType;
import com.donat.donchess.domain.enums.SpecialMoveType;
import com.donat.donchess.model.objects.ChessTable;
import com.donat.donchess.model.objects.Figure;
import com.donat.donchess.model.enums.ChessFigure;
import com.donat.donchess.model.enums.Color;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.inject.Provider;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.*;

@Service
@Transactional
public class TableBuilderService {

	@Autowired
	private Provider<EntityManager> entityManager;

	public ChessTable buildTable(Long chessGameId) {
		JPAQueryFactory query = new JPAQueryFactory(entityManager);
		QChessMove chessMoveFromQ = QChessMove.chessMove;
		QChessGame chessGameFromQ = QChessGame.chessGame;

		ChessTable chessTable = new ChessTable();

		ChessGame chessGame = query
			.selectFrom(chessGameFromQ)
			.where(chessGameFromQ.id.eq(chessGameId))
			.fetchOne();

		List<ChessMove> chessMoves = query
			.selectFrom(chessMoveFromQ)
			.orderBy(chessMoveFromQ.moveId.asc())
			.where(chessMoveFromQ.chessGame.id.eq(chessGameId))
			.fetch();

		chessTable.setChessGameId(chessGameId);
		chessTable.setWhoIsNext(chessGame.getNextMove());
		chessTable.setActualMoveNumber(chessGame.getLastMoveId());

		initChessTable(chessTable, chessGame);
		for (ChessMove chessMove : chessMoves) {
			makeMove(chessTable, chessMove);
		}

		return chessTable;
	}

	public void makeMove(ChessTable chessTable, ChessMove chessMove) {
		//simple move
		Figure figureToMove = findFigure(chessTable.getFigures(), chessMove.getMoveFromX(), chessMove.getMoveFromY());
		Figure figureToKill = findFigure(chessTable.getFigures(), chessMove.getMoveToX(), chessMove.getMoveToY());
		figureToMove.setCoordX(chessMove.getMoveToX());
		figureToMove.setCoordY(chessMove.getMoveToY());
		figureToMove.setMoved(true);
		chessTable.setLastMoveWasDoublePawn(false);
		if (figureToMove.getFigureType().equals(ChessFigure.PAWN)) {
			chessTable.setLastPawnMoveNumber(chessMove.getMoveId());
			if (Math.abs(chessMove.getMoveToY() - chessMove.getMoveFromY()) > 1) {
				chessTable.setLastMoveWasDoublePawn(true);
				chessTable.setColumnIndexIfLastMoveWasDoublePawn(chessMove.getMoveToX());
			}
		}

		boolean chessPieceTaken = false;
		if (figureToKill != null) {
			chessTable.getFigures().remove(figureToKill);
			chessTable.setLastHitMoveNumber(chessMove.getMoveId());
			chessPieceTaken = true;
		}

		//en-passan
		if (chessMove.getSpecialMoveType().equals(SpecialMoveType.EN_PASSAN)) {
			Figure figureToKillDuringEnPassan =
				findFigure(chessTable.getFigures(), chessMove.getMoveToX(), chessMove.getMoveFromY());

			chessTable.getFigures().remove(figureToKillDuringEnPassan);
			chessPieceTaken = true;
		}

		//castling
		if (chessMove.getSpecialMoveType().equals(SpecialMoveType.CASTLING)) {
			int expectedXofRook = chessMove.getMoveToX() > chessMove.getMoveFromX() ?
				8 : 1;
			int targetXofRook = chessMove.getMoveToX() > chessMove.getMoveFromX() ?
				6 : 4;
			Figure rookToMoveDueToCastling =
				findFigure(chessTable.getFigures(), expectedXofRook, chessMove.getMoveFromY());
			rookToMoveDueToCastling.setCoordX(targetXofRook);
		}

		//promoting
		if (chessMove.getPromoteType() != null) {
			figureToMove.setFigureType(chessMove.getPromoteType());
		}

		//in case of chess, the moveAlready flag of the King should be set true
		setToMovedTheKingInCaseOfChess(chessTable, chessMove);

		chessTable.setDrawWasOffered(chessMove.getDrawOffered() == null ? false : chessMove.getDrawOffered());

		//moveHistory
		StringBuffer actualMove = new StringBuffer();
		if (chessTable.getMoveHistory() != null) {
			actualMove.append(chessTable.getMoveHistory());
		}
		if (chessMove.getMoveId() % 2 == 1) {
			actualMove.append((chessMove.getMoveId() + 1) / 2);
            actualMove.append(".");
		} else {
			actualMove.append(" - ");
		}
		actualMove.append(transLateColumn(chessMove.getMoveFromX()));
		actualMove.append(chessMove.getMoveFromY());
		actualMove.append(chessPieceTaken ? "x" : "-");
		actualMove.append(transLateColumn(chessMove.getMoveToX()));
		actualMove.append(chessMove.getMoveToY());
		if (Objects.equals(Boolean.TRUE, chessMove.isChessGiven())) {
			actualMove.append("!");
		}
		if (chessMove.getMoveId() % 2 == 0) {
			actualMove.append("\r\n");
		}

		chessTable.setMoveHistory(actualMove.toString());

	}

	private String transLateColumn(Integer coord) {
		if (coord.equals(1)) {
			return "A";
		} else if(coord.equals(2)) {
			return "B";
		} else if(coord.equals(3)) {
			return "C";
		} else if(coord.equals(4)) {
			return "D";
		} else if(coord.equals(5)) {
			return "E";
		} else if(coord.equals(6)) {
			return "F";
		} else if(coord.equals(7)) {
			return "G";
		}
		return "H";
	}

	private void setToMovedTheKingInCaseOfChess(ChessTable chessTable, ChessMove chessMove) {
		if (Objects.equals(Boolean.TRUE, chessMove.isChessGiven())) {
			for (Figure enemyKing : chessTable.getFigures()) {
				if (!enemyKing.getColor().equals(chessTable.getWhoIsNext()) &&
					enemyKing.getFigureType().equals(ChessFigure.KING)) {
					enemyKing.setMoved(true);
					break;
				}
			}
		}
	}

	public Figure findFigure(Set<Figure> figures, Integer coordX, Integer coordY) {
		return figures
			.stream()
			.filter(figure -> figure.getCoordX() == coordX &&
				figure.getCoordY() == coordY)
			.findAny().orElse(null);
	}

	public void initChessTable(ChessTable chessTable, ChessGame chessGame) {
		if (chessGame.getChessGameType().equals(ChessGameType.NORMAL)) {
			chessTable.setFigures(normalInitChessSet());
		}
	}

	public Set<Figure> normalInitChessSet() {
		Set<Figure> figures = new HashSet<>();
		//Pawns
		for (int i = 1; i <= 8; i++) {
			Figure whitePawn = new Figure(ChessFigure.PAWN, Color.WHITE, i, 2);
			Figure blackPawn = new Figure(ChessFigure.PAWN, Color.BLACK, i, 7);
			figures.add(whitePawn);
			figures.add(blackPawn);
		}
		//Rooks
		Figure whiteRook1 = new Figure(ChessFigure.ROOK, Color.WHITE, 1, 1);
		Figure whiteRook2 = new Figure(ChessFigure.ROOK, Color.WHITE, 8, 1);
		Figure blackRook1 = new Figure(ChessFigure.ROOK, Color.BLACK, 1, 8);
		Figure blackRook2 = new Figure(ChessFigure.ROOK, Color.BLACK, 8, 8);
		figures.add(whiteRook1);
		figures.add(whiteRook2);
		figures.add(blackRook1);
		figures.add(blackRook2);
		//Knights
		Figure whiteKnight1 = new Figure(ChessFigure.KNIGHT, Color.WHITE, 2, 1);
		Figure whiteKnight2 = new Figure(ChessFigure.KNIGHT, Color.WHITE, 7, 1);
		Figure blackKnight1 = new Figure(ChessFigure.KNIGHT, Color.BLACK, 2, 8);
		Figure blackKnight2 = new Figure(ChessFigure.KNIGHT, Color.BLACK, 7, 8);
		figures.add(whiteKnight1);
		figures.add(whiteKnight2);
		figures.add(blackKnight1);
		figures.add(blackKnight2);
		//Bishops
		Figure whiteBishop1 = new Figure(ChessFigure.BISHOP, Color.WHITE, 3, 1);
		Figure whiteBishop2 = new Figure(ChessFigure.BISHOP, Color.WHITE, 6, 1);
		Figure blackBishop1 = new Figure(ChessFigure.BISHOP, Color.BLACK, 3, 8);
		Figure blackBishop2 = new Figure(ChessFigure.BISHOP, Color.BLACK, 6, 8);
		figures.add(whiteBishop1);
		figures.add(whiteBishop2);
		figures.add(blackBishop1);
		figures.add(blackBishop2);
		//Queens
		Figure whiteQueen = new Figure(ChessFigure.QUEEN, Color.WHITE, 4, 1);
		Figure blackQueen = new Figure(ChessFigure.QUEEN, Color.BLACK, 4, 8);
		figures.add(whiteQueen);
		figures.add(blackQueen);
		//Kings
		Figure whiteKing = new Figure(ChessFigure.KING, Color.WHITE, 5, 1);
		Figure blackKing = new Figure(ChessFigure.KING, Color.BLACK, 5, 8);
		figures.add(whiteKing);
		figures.add(blackKing);

		return figures;
	}

}
