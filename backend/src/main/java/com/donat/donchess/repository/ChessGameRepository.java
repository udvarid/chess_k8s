package com.donat.donchess.repository;

import com.donat.donchess.domain.ChessGame;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ChessGameRepository extends JpaRepository<ChessGame, Long>, JpaSpecificationExecutor<ChessGame> {
}
