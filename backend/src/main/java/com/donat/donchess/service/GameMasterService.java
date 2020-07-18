package com.donat.donchess.service;

import com.donat.donchess.domain.*;
import com.donat.donchess.domain.enums.ChessGameStatus;
import com.donat.donchess.domain.enums.Result;
import com.donat.donchess.dto.User.UserDto;
import com.donat.donchess.dto.chessGame.*;
import com.donat.donchess.exceptions.InvalidException;
import com.donat.donchess.exceptions.NotFoundException;
import com.donat.donchess.model.enums.ChessFigure;
import com.donat.donchess.model.enums.Color;
import com.donat.donchess.model.logic.DrawJudge;
import com.donat.donchess.model.logic.MoveValidator;
import com.donat.donchess.model.logic.ValidMoveInspector;
import com.donat.donchess.model.objects.ChessTable;
import com.donat.donchess.model.objects.Coordinate;
import com.donat.donchess.model.objects.Figure;
import com.donat.donchess.model.objects.ValidMove;
import com.donat.donchess.repository.ChessGameRepository;
import com.donat.donchess.repository.ChessMoveRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
//import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import javax.inject.Provider;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class GameMasterService {

	private TableBuilderService tableBuilderService;
	private MoveValidator moveValidator;
	private DrawJudge drawJudge;
	private SecurityService securityService;
	private ChessGameRepository chessGameRepository;
	private ChessMoveRepository chessMoveRepository;
	private ValidMoveInspector validMoveInspector;
	private Provider<EntityManager> entityManager;
	//private SimpMessagingTemplate template;


	public GameMasterService(TableBuilderService tableBuilderService,
		MoveValidator moveValidator,
		DrawJudge drawJudge,
		SecurityService securityService,
		ChessGameRepository chessGameRepository,
		ChessMoveRepository chessMoveRepository,
		ValidMoveInspector validMoveInspector,
		Provider<EntityManager> entityManager/*,
		SimpMessagingTemplate template*/) {
		this.tableBuilderService = tableBuilderService;
		this.moveValidator = moveValidator;
		this.drawJudge = drawJudge;
		this.securityService = securityService;
		this.chessGameRepository = chessGameRepository;
		this.chessMoveRepository = chessMoveRepository;
		this.validMoveInspector = validMoveInspector;
		this.entityManager = entityManager;
		//this.template = template;
	}

	public ResultDto handleMove(ChessMoveDto chessMoveDto, boolean isAPlayer) {


		ChessGame chessGame = chessGameRepository.findById(chessMoveDto.getGameId())
			.orElseThrow(() -> new NotFoundException("Game can not be found"));

		if (isAPlayer) {
			User user = securityService.getChallenger();
			commonExceptionHandling(user, chessGame);
		}

		if (chessMoveDto.getMoveId() != chessGame.getLastMoveId() + 1) {
			throw new InvalidException("This is not the proper move id!");
		}

		if (!validPromoteType(chessMoveDto)) {
			throw new InvalidException("Not valid promotion type!");
		}

		ChessTable chessTable = tableBuilderService.buildTable(chessMoveDto.getGameId());

		ValidMove validMove = moveValidator.validmove(chessTable, chessMoveDto);

		ResultDto resultDto = new ResultDto();

		if (validMove != null) {
			ChessGame chessGameSaved = makeMove(chessGame, chessTable, chessMoveDto, validMove);
			resultDto.setResult(chessGameSaved.getResult());
			resultDto.setUserOne(chessGameSaved.getUserOne().getFullname());
			resultDto.setUserTwo(chessGameSaved.getUserTwo().getFullname());
		} else {
			throw new InvalidException("Not valid move");
		}

		//template.convertAndSend("/topic/notification", chessGame.getId());

		return resultDto;
	}

	private void commonExceptionHandling(User user, ChessGame chessGame) {
		if (!userIsPlaying(user, chessGame)) {
			throw new InvalidException("Player is not playing in this game");
		}

		if (chessGame.getChessGameStatus().equals(ChessGameStatus.CLOSED)) {
			throw new InvalidException("The game is not active any more");
		}
		if (!userIsNextPlayer(user, chessGame)) {
			throw new InvalidException("The next move is expected from the other player!");
		}
	}


	private boolean userIsNextPlayer(User user, ChessGame chessGame) {
		return chessGame.getNextMove().equals(Color.WHITE) &&
			user.getId().equals(chessGame.getUserOne().getId())
			||
			chessGame.getNextMove().equals(Color.BLACK) &&
				user.getId().equals(chessGame.getUserTwo().getId());
	}

	private boolean userIsPlaying(User user, ChessGame chessGame) {
		return chessGame.getUserOne().getId().equals(user.getId()) ||
			chessGame.getUserTwo().getId().equals(user.getId());
	}

	private boolean validPromoteType(ChessMoveDto chessMoveDto) {
		ChessFigure chessFigure = null;
		if (chessMoveDto.getPromoteToFigure() != null &&
			!chessMoveDto.getPromoteToFigure().isEmpty()) {
			chessFigure = ChessFigure.valueOf(chessMoveDto.getPromoteToFigure());
		}
		return chessFigure == null ||
			!(chessFigure.equals(ChessFigure.PAWN) || chessFigure.equals(ChessFigure.KING));
	}

	public ChessGame makeMove(ChessGame chessGame, ChessTable chessTable, ChessMoveDto chessMoveDto, ValidMove validMove) {
		Figure figure = validMoveInspector
			.findFigure(chessTable.getFigures(),
				new Coordinate(chessMoveDto.getMoveFromX(), chessMoveDto.getMoveFromY()));
		Figure aimFigure = validMoveInspector
			.findFigure(chessTable.getFigures(),
				new Coordinate(chessMoveDto.getMoveToX(), chessMoveDto.getMoveToY()));

		setChessTable(chessTable, chessMoveDto, figure, aimFigure);

		setMoveOfFigure(chessMoveDto, figure);

		ChessMove chessMove = createChessMove(chessGame, chessTable, chessMoveDto, validMove, figure);
		chessMoveRepository.saveAndFlush(chessMove);

		setChessGame(chessGame, chessTable, chessMove);
		return chessGameRepository.saveAndFlush(chessGame);
	}

	private void setChessTable(ChessTable chessTable, ChessMoveDto chessMoveDto, Figure figure, Figure aimFigure) {
		chessTable.setWhoIsNext(changeColor(chessTable.getWhoIsNext()));
		chessTable.setActualMoveNumber(chessTable.getActualMoveNumber() + 1);
		if (figure.getFigureType().equals(ChessFigure.PAWN)) {
			chessTable.setLastPawnMoveNumber(chessTable.getActualMoveNumber());
			if (Math.abs(chessMoveDto.getMoveFromY() - chessMoveDto.getMoveToY()) > 1) {
				chessTable.setLastMoveWasDoublePawn(true);
				chessTable.setColumnIndexIfLastMoveWasDoublePawn(figure.getCoordX());
			}
		}

		if (aimFigure != null) {
			chessTable.getFigures().remove(aimFigure);
			chessTable.setLastHitMoveNumber(chessTable.getActualMoveNumber());
		}
	}

	private void setMoveOfFigure(ChessMoveDto chessMoveDto, Figure figure) {
		figure.setCoordX(chessMoveDto.getMoveToX());
		figure.setCoordY(chessMoveDto.getMoveToY());
		figure.setMoved(true);
		if (chessMoveDto.getPromoteToFigure() != null && !chessMoveDto.getPromoteToFigure().isEmpty()) {
			figure.setFigureType(ChessFigure.valueOf(chessMoveDto.getPromoteToFigure()));
		}
	}

	private ChessMove createChessMove(ChessGame chessGame, ChessTable chessTable, ChessMoveDto chessMoveDto, ValidMove validMove, Figure figure) {
		ChessMove chessMove = new ChessMove();
		chessMove.setSpecialMoveType(validMove.getSpecialMoveType());
		if (chessMoveDto.getPromoteToFigure() != null && !chessMoveDto.getPromoteToFigure().isEmpty()) {
			chessMove.setPromoteType(ChessFigure.valueOf(chessMoveDto.getPromoteToFigure()));
		}
		chessMove.setChessGame(chessGame);
		chessMove.setMoveFromX(chessMoveDto.getMoveFromX());
		chessMove.setMoveFromY(chessMoveDto.getMoveFromY());
		chessMove.setMoveToX(chessMoveDto.getMoveToX());
		chessMove.setMoveToY(chessMoveDto.getMoveToY());
		chessMove.setMoveId(chessGame.getLastMoveId() + 1);
		chessMove.setChessGiven(chessGiven(chessTable, figure));
		chessMove.setDrawOffered(chessMoveDto.isDrawOffered());
		return chessMove;
	}

	private void setChessGame(ChessGame chessGame, ChessTable chessTable, ChessMove chessMove) {
		if (!chessMove.isChessGiven() && drawJudge.checkDraw(chessTable)) {
			chessGame.setChessGameStatus(ChessGameStatus.CLOSED);
			chessGame.setResult(Result.DRAWN);
			chessGame.setFinishTime(LocalDateTime.now());
		}
		if (chessMove.isChessGiven() && drawJudge.noPossibleMove(chessTable)) {
			chessGame.setChessGameStatus(ChessGameStatus.CLOSED);
			chessGame.setResult(chessTable.getWhoIsNext().equals(Color.WHITE) ? Result.WON_USER_TWO : Result.WON_USER_ONE);
			chessGame.setFinishTime(LocalDateTime.now());
		}
		chessGame.getChessMoves().add(chessMove);
		chessGame.setLastMoveId(chessGame.getLastMoveId() + 1);
		chessGame.setNextMove(changeColor(chessGame.getNextMove()));
	}

	private Boolean chessGiven(ChessTable chessTable, Figure figure) {

		Set<ValidMove> validMoves = validMoveInspector.allValidMoves(chessTable,
			new Coordinate(figure.getCoordX(), figure.getCoordY()));

		Figure enemyKing = chessTable
			.getFigures()
			.stream()
			.filter(king -> king.getFigureType().equals(ChessFigure.KING) &&
				!king.getColor().equals(figure.getColor()))
			.findFirst()
			.orElseThrow(() -> new NotFoundException("King not found!"));

		return validMoves
			.stream()
			.anyMatch(vm -> vm.getCoordinate().equals(new Coordinate(enemyKing.getCoordX(), enemyKing.getCoordY())));


	}

	private Color changeColor(Color nextMove) {
		return nextMove.equals(Color.WHITE) ? Color.BLACK : Color.WHITE;
	}

	public List<ChessGameDto> findChessgames(boolean everyBody) {
		List<ChessGameDto> chessGameDtos = new ArrayList<>();

		List<ChessGame> chessGames = chessGameRepository.findAll();

		if (!everyBody) {
			chessGames = chessGames.stream()
				.filter(chessGame -> chessGame.getChessGameStatus().equals(ChessGameStatus.OPEN))
				.collect(Collectors.toList());
		}

		for (ChessGame chessGame : chessGames) {
			ChessGameDto chessGameDto = new ChessGameDto();
			chessGameDto.setChessGameId(chessGame.getId());
			chessGameDto.setChessGameStatus(chessGame.getChessGameStatus());
			chessGameDto.setChessGameType(chessGame.getChessGameType());
			chessGameDto.setLastMoveId(chessGame.getLastMoveId());
			chessGameDto.setNextMove(chessGame.getNextMove());
			chessGameDto.setResult(chessGame.getResult());
			chessGameDto.setUserOne(userDtoMapper(chessGame.getUserOne()));
			chessGameDto.setUserTwo(userDtoMapper(chessGame.getUserTwo()));

			chessGameDtos.add(chessGameDto);
		}

		return chessGameDtos;
	}


	public ChessTableDto giveChessTable(long chessGameId) {
		ChessTableDto chessTableDto = new ChessTableDto();
		JPAQueryFactory query = new JPAQueryFactory(entityManager);

		QChessGame chessGameFromQ = QChessGame.chessGame;
		ChessGame chessGame = query.selectFrom(chessGameFromQ)
			.where(chessGameFromQ.id.eq(chessGameId))
			.fetchOne();

		ChessTable chessTable = tableBuilderService.buildTable(chessGameId);

		chessTableDto.setChessGameId(chessGameId);
		chessTableDto.setChessGameStatus(chessGame.getChessGameStatus());
		chessTableDto.setChessGameType(chessGame.getChessGameType());
		chessTableDto.setLastMoveId(chessGame.getLastMoveId());
		chessTableDto.setResult(chessGame.getResult());
		chessTableDto.setNextMove(chessGame.getNextMove());
		chessTableDto.setUserOne(userDtoMapper(chessGame.getUserOne()));
		chessTableDto.setUserTwo(userDtoMapper(chessGame.getUserTwo()));
		chessTableDto.setFigures(chessTable
			.getFigures()
			.stream()
			.map(figure -> figureDtoMapper(figure))
			.collect(Collectors.toSet()));
		chessTableDto.setDrawOffered(chessTable.isDrawWasOffered());

		chessTableDto.setMoveHistory(chessTable.getMoveHistory());

		return chessTableDto;
	}

	private FigureDto figureDtoMapper(Figure figure) {
		FigureDto figureDto = new FigureDto();
		figureDto.setColor(figure.getColor());
		figureDto.setCoordX(figure.getCoordX());
		figureDto.setCoordY(figure.getCoordY());
		figureDto.setFigureType(figure.getFigureType());
		figureDto.setMoved(figure.isMoved());

		return figureDto;
	}

	private UserDto userDtoMapper(User user) {
		UserDto userDto = new UserDto();
		userDto.setFullName(user.getFullname());
		userDto.setId(user.getId());
		Role role = (Role) user.getRoles().toArray()[0];
		userDto.setRole(role.getRole());

		return userDto;
	}


	public ValidMovesDto giveValidMoves(long chessGameId) {
		ChessGame chessGame = chessGameRepository.findById(chessGameId)
			.orElseThrow(() -> new NotFoundException("ChessGame id is not valid"));

		if (!chessGame.getChessGameStatus().equals(ChessGameStatus.OPEN)) {
			ValidMovesDto validMovesDto = new ValidMovesDto();
			validMovesDto.setChessGameId(chessGameId);
			return validMovesDto;
		}
		return createValidMoves(tableBuilderService.buildTable(chessGameId));
	}

	private ValidMovesDto createValidMoves(ChessTable chessTable) {
		ValidMovesDto validMovesDto = new ValidMovesDto();
		validMovesDto.setChessGameId(chessTable.getChessGameId());
		List<CoordinateDto> coordinateDtos = new ArrayList<>();
		for (Figure figure : filterFigureByColor(chessTable)) {
			Set<ValidMove> validMoves = validMoveInspector
				.allValidMoves(chessTable, new Coordinate(figure.getCoordX(), figure.getCoordY()));
			Set<ValidMove> legalMoves = validMoves.stream()
				.filter(vm -> moveValidator.validmove(chessTable,
					setChessMoveDto(chessTable.getChessGameId(), chessTable, figure, vm)) != null)
				.collect(Collectors.toSet());
			legalMoves
				.forEach(lm -> coordinateDtos.add(coordinateDtoMapper(lm, figure)));
		}
		validMovesDto.setValidMoves(coordinateDtos);
		return validMovesDto;
	}


	private CoordinateDto coordinateDtoMapper(ValidMove lm, Figure figure) {
		CoordinateDto coordinateDto = new CoordinateDto();

		coordinateDto.setFromX(figure.getCoordX());
		coordinateDto.setFromY(figure.getCoordY());
		coordinateDto.setToX(lm.getCoordinate().getX());
		coordinateDto.setToY(lm.getCoordinate().getY());

		return coordinateDto;
	}

	private ChessMoveDto setChessMoveDto(long chessGameId, ChessTable chessTable, Figure figure, ValidMove vm) {
		ChessMoveDto chessMoveDto = new ChessMoveDto();
		chessMoveDto.setGameId(chessGameId);
		chessMoveDto.setMoveFromX(figure.getCoordX());
		chessMoveDto.setMoveFromY(figure.getCoordY());
		chessMoveDto.setMoveToX(vm.getCoordinate().getX());
		chessMoveDto.setMoveToY(vm.getCoordinate().getY());
		chessMoveDto.setMoveId(chessTable.getActualMoveNumber());
		return chessMoveDto;
	}

	private Set<Figure> filterFigureByColor(ChessTable chessTable) {
		return chessTable.getFigures()
			.stream()
			.filter(figure -> figure.getColor().equals(chessTable.getWhoIsNext()))
			.collect(Collectors.toSet());
	}

	public List<ChessGameDto> findChessgamesForRequester() {
		User user = securityService.getChallenger();

		if (user == null) {
			throw new InvalidException("You have to log in");
		}

		return findChessgames(true)
			.stream()
			.filter(chessGame -> chessGame.getUserOne().getId().equals(user.getId()) || chessGame.getUserTwo().getId().equals(user.getId()))
			.collect(Collectors.toList());
	}

	public ResultDto giveUp(long chessGameId) {
		return finishByUser(chessGameId, false);

	}

	public ResultDto acceptDraw(long chessGameId) {
		return finishByUser(chessGameId, true);
	}

	public ResultDto finishByUser(long chessGameId, boolean isDraw) {
		User user = securityService.getChallenger();

		ChessGame chessGame = chessGameRepository.findById(chessGameId)
			.orElseThrow(() -> new NotFoundException("Game can not be found"));

		if (!userIsPlaying(user, chessGame)) {
			throw new InvalidException("Player is not playing in this game");
		}

		commonExceptionHandling(user, chessGame);

		chessGame.setChessGameStatus(ChessGameStatus.CLOSED);

		if (isDraw) {
			chessGame.setResult(Result.DRAWN);
		} else {
			chessGame.setResult(chessGame.getNextMove().equals(Color.WHITE) ?
				Result.WON_USER_TWO : Result.WON_USER_ONE);
		}

		chessGame.setFinishTime(LocalDateTime.now());

		ChessGame chessGameSaved = chessGameRepository.saveAndFlush(chessGame);

		ResultDto resultDto = new ResultDto();
		resultDto.setResult(chessGameSaved.getResult());
		resultDto.setUserOne(chessGameSaved.getUserOne().getFullname());
		resultDto.setUserTwo(chessGameSaved.getUserTwo().getFullname());

		return resultDto;
	}
}
