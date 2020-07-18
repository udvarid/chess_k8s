package com.donat.donchess.controller;

import com.donat.donchess.dto.chessGame.ChessGameDto;
import com.donat.donchess.dto.chessGame.ChessMoveDto;
import com.donat.donchess.dto.chessGame.ChessTableDto;
import com.donat.donchess.dto.chessGame.ResultDto;
import com.donat.donchess.dto.chessGame.ValidMovesDto;
import com.donat.donchess.service.GameMasterService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Transactional
@RequestMapping("/api/game")
public class GameController {

    private GameMasterService gameMasterService;

    public GameController(GameMasterService gameMasterService) {
        this.gameMasterService = gameMasterService;
    }

    @PostMapping("/move")
    public ResultDto create(@RequestBody ChessMoveDto chessMoveDto) {
        return gameMasterService.handleMove(chessMoveDto, true);
    }

    @GetMapping
    public List<ChessGameDto> allChessGame() {
        return gameMasterService.findChessgames(false);
    }

    @GetMapping("/giveUp/{id}")
    public ResultDto giveUp(@PathVariable("id") long chessGameId) {
        return gameMasterService.giveUp(chessGameId);
    }

	@GetMapping("/acceptDraw/{id}")
	public ResultDto acceptDraw(@PathVariable("id") long chessGameId) {
		return gameMasterService.acceptDraw(chessGameId);
	}

    @GetMapping("/list")
    public List<ChessGameDto> allChessGameForRequester() {
        return gameMasterService.findChessgamesForRequester();
    }

    @GetMapping("/{id}")
    public ChessTableDto findChessGame(@PathVariable("id") long chessGameId) {
        return gameMasterService.giveChessTable(chessGameId);
    }

    @GetMapping("/validMoves/{id}")
    public ValidMovesDto findValidMoves(@PathVariable("id") long chessGameId) {
        return gameMasterService.giveValidMoves(chessGameId);
    }

}
