package com.donat.donchess.AI;

import com.donat.donchess.dto.chessGame.CoordinateDto;
import com.donat.donchess.model.objects.ChessTable;

public interface Evaluator {
	int giveScore(CoordinateDto validMove, ChessTable chessTable);
}
