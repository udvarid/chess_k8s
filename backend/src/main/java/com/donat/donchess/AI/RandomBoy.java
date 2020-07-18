package com.donat.donchess.AI;

import com.donat.donchess.dto.chessGame.CoordinateDto;
import com.donat.donchess.model.objects.ChessTable;

public class RandomBoy extends EvaluatorWeight implements Evaluator {

	public RandomBoy(int weight) {
		super(weight);
	}

	@Override
	public int giveScore(CoordinateDto validMove, ChessTable chessTable) {
		return 0;
	}
}
