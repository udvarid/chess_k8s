package com.donat.donchess.repository;

import org.springframework.data.jpa.domain.Specification;
import com.donat.donchess.domain.ChessGame;
import com.donat.donchess.domain.enums.ChessGameStatus;

public interface ChessGameSpecifications {

	static Specification<ChessGame> openGame() {
		return (root, query, criteriaBuilder) ->
			criteriaBuilder.equal(root.get("chessGameStatus"), ChessGameStatus.OPEN);
	}

}
