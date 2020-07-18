package com.donat.donchess.repository;

import com.donat.donchess.domain.ChessMove;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChessMoveRepository extends JpaRepository<ChessMove, Long> {
}
