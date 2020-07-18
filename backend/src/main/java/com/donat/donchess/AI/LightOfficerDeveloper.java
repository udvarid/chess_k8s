package com.donat.donchess.AI;

import com.donat.donchess.dto.chessGame.CoordinateDto;
import com.donat.donchess.model.enums.ChessFigure;
import com.donat.donchess.model.enums.Color;
import com.donat.donchess.model.objects.ChessTable;
import com.donat.donchess.model.objects.Figure;

public class LightOfficerDeveloper extends EvaluatorWeight implements Evaluator {

	public LightOfficerDeveloper(int weight) {
		super(weight);
	}

	@Override
	public int giveScore(CoordinateDto validMove, ChessTable chessTable) {
		int score = calculateInitScore(chessTable) + currentMoveScoreIfLightOfficer(validMove, chessTable);
		return score * getWeight();
	}

	private int currentMoveScoreIfLightOfficer(CoordinateDto validMove, ChessTable chessTable) {
		Figure figure = findFigure(validMove, chessTable);
		if (figureIsLightOfficer(figure) && !figure.isMoved()) {
			return 250;
		}
		return 0;
	}

	private Figure findFigure(CoordinateDto validMove, ChessTable chessTable) {
		return chessTable.getFigures()
			.stream()
			.filter(f -> f.getCoordX() == validMove.getFromX() &&
				f.getCoordY() == validMove.getFromY())
			.findFirst()
			.orElse(null);
	}

	private int calculateInitScore(ChessTable chessTable) {
		int blackScore = 0;
		int whiteScore = 0;
		for (Figure figure : chessTable.getFigures()) {
			if (figureIsLightOfficer(figure) && figure.isMoved()) {
				if (figure.getClass().equals(Color.BLACK)) {
					blackScore += 250;
				} else {
					whiteScore += 250;
				}
			}
		}
		return chessTable.getWhoIsNext().equals(Color.WHITE) ?
			whiteScore - blackScore : blackScore - whiteScore;
	}

	private boolean figureIsLightOfficer(Figure figure) {
		return figure.getFigureType().equals(ChessFigure.KNIGHT) || figure.getFigureType().equals(ChessFigure.BISHOP);
	}
}
