package com.donat.donchess.service;

import java.util.List;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.donat.donchess.domain.enums.ChessGameStatus;
import com.donat.donchess.dto.User.UserDto;
import com.donat.donchess.dto.chessGame.ChessGameDto;
import com.donat.donchess.dto.chessGame.ChessMoveDto;
import com.donat.donchess.model.enums.Color;

@Service
@Transactional
public class BotService {

	@Autowired
	private GameMasterService gameMasterService;

	@Autowired
	private BotStrategyService botStrategyService;

	public void makeMove() {
		List<ChessGameDto> games = gameMasterService.findChessgames(false)
			.stream()
			.filter(game -> botIsNext(game) && game.getChessGameStatus().equals(ChessGameStatus.OPEN))
			.collect(Collectors.toList());

		for (ChessGameDto game : games) {
			ChessMoveDto chessMoveDto = botStrategyService.giveMove(game);
			gameMasterService.handleMove(chessMoveDto, false);
		}
	}

	private boolean botIsNext(ChessGameDto game) {
		return game.getNextMove().equals(Color.WHITE) && isBot(game.getUserOne()) ||
			game.getNextMove().equals(Color.BLACK) && isBot(game.getUserTwo());
	}

	private boolean isBot(UserDto user) {
		return !user.getRole().equals("ROLE_ADMIN") &&
			!user.getRole().equals("ROLE_USER");
	}

}

