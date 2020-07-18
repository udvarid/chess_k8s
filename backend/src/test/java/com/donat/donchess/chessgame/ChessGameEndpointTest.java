package com.donat.donchess.chessgame;

import com.donat.donchess.AbstractApiTest;
import com.donat.donchess.domain.enums.ChessGameStatus;
import com.donat.donchess.domain.enums.ChessGameType;
import com.donat.donchess.domain.enums.Result;
import com.donat.donchess.dto.User.UserDto;
import com.donat.donchess.dto.challange.ChallengeActionDto;
import com.donat.donchess.dto.challange.ChallengeCreateDto;
import com.donat.donchess.dto.challange.ChallengeDto;
import com.donat.donchess.dto.chessGame.*;
import com.donat.donchess.model.enums.ChessFigure;
import com.donat.donchess.model.enums.Color;
import com.donat.donchess.model.logic.ValidMoveInspector;
import com.donat.donchess.model.objects.Coordinate;
import com.donat.donchess.model.objects.Figure;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;


public class ChessGameEndpointTest extends AbstractApiTest {

    @Autowired
    ValidMoveInspector validMoveInspector;

    @Test
    public void makeMoveTest() {
        loginAsDonat1();
        ChessTableDto initStanding = gameApi.getOneChessGame(1);
        ChessMoveDto chessMoveDto = new ChessMoveDto();
        chessMoveDto.setGameId(initStanding.getChessGameId());
        chessMoveDto.setMoveId(initStanding.getLastMoveId() + 1);
        chessMoveDto.setMoveFromX(6);
        chessMoveDto.setMoveFromY(2);
        chessMoveDto.setMoveToX(6);
        chessMoveDto.setMoveToY(3);
        gameApi.chessMove(chessMoveDto);
        ChessTableDto afterStanding = gameApi.getOneChessGame(1);

        assertEquals(initStanding.getChessGameId(), afterStanding.getChessGameId());
        assertEquals(initStanding.getLastMoveId() + 1, afterStanding.getLastMoveId());
        assertNotEquals(initStanding.getNextMove(), afterStanding.getNextMove());
        assertEquals(afterStanding.getResult(), Result.OPEN);
        assertEquals(initStanding.getFigures().size(), afterStanding.getFigures().size());
        for (FigureDto figure : afterStanding.getFigures()) {
            if (figure.getCoordX() == 6 && figure.getCoordY() == 3) {
                assertFalse(theSameFigure(figure, initStanding.getFigures()));
            } else {
                assertTrue(theSameFigure(figure, initStanding.getFigures()));
            }
        }
    }

    @Test
    public void listOfGamesTest() {
        loginAsDonat1();
        List<ChessGameDto> initGames = gameApi.getAll();
        ChallengeCreateDto challengeCreateDto = new ChallengeCreateDto();
        challengeCreateDto.setChallengedId(2l);
        challengeApi.create(challengeCreateDto);

        loginAsDonat2();
        ChallengeActionDto challengeActionDto = new ChallengeActionDto();
        List<ChallengeDto> allChallenges = challengeApi.getAll();
        challengeActionDto.setChallengeId(allChallenges.get(0).getId());
        challengeActionDto.setChallengeAction("ACCEPT");
        challengeApi.answer(challengeActionDto);

        List<ChallengeDto> allChallengesAfterAccept = challengeApi.getAll();
        List<ChessGameDto> gamesAfterAccept = gameApi.getAll();

        assertEquals(initGames.size() + 1, gamesAfterAccept.size());
        assertEquals(0, allChallengesAfterAccept.size());

        ChessGameDto chessGameDto = getNewGame(initGames, gamesAfterAccept);
        if (chessGameDto != null) {
            assertEquals(ChessGameStatus.OPEN, chessGameDto.getChessGameStatus());
            assertEquals(ChessGameType.NORMAL, chessGameDto.getChessGameType());
            assertEquals(Result.OPEN, chessGameDto.getResult());
            assertEquals(Color.WHITE, chessGameDto.getNextMove());
        }

    }

    @Test
    public void validMovesTest() {
        loginAsDonat1();
        List<ChessGameDto> initGames = gameApi.getAll();
        ChallengeCreateDto challengeCreateDto = new ChallengeCreateDto();
        challengeCreateDto.setChallengedId(2l);
        challengeApi.create(challengeCreateDto);

        loginAsDonat2();
        ChallengeActionDto challengeActionDto = new ChallengeActionDto();
        List<ChallengeDto> allChallenges = challengeApi.getAll();
        challengeActionDto.setChallengeId(allChallenges.get(0).getId());
        challengeActionDto.setChallengeAction("ACCEPT");
        challengeApi.answer(challengeActionDto);
        List<ChessGameDto> gamesAfterAccept = gameApi.getAll();

        ChessGameDto chessGameDto = getNewGame(initGames, gamesAfterAccept);
        ValidMovesDto validMoves = gameApi.getValidMoves(chessGameDto.getChessGameId());
        ChessTableDto chessGame = gameApi.getOneChessGame(chessGameDto.getChessGameId());

        assertNotNull(validMoves);
        assertEquals(20, validMoves.getValidMoves().size());

        //annak validálása, hogy csak a fehér gyalogok és a fehér lovasok tudnak lépni
        for (CoordinateDto validMove : validMoves.getValidMoves()) {
            FigureDto figure = getFigureBasedValidMoves(chessGame.getFigures(), validMove);
            assertNotNull(figure);
            assertEquals(Color.WHITE, figure.getColor());
            assertTrue(figure.getFigureType().equals(ChessFigure.PAWN) ||
                    figure.getFigureType().equals(ChessFigure.KNIGHT));
        }


    }

    @Test
    public void validPromotingTest() {
        loginAsDonat1();
        List<ChessGameDto> initGames = gameApi.getAll();
        ChallengeCreateDto challengeCreateDto = new ChallengeCreateDto();
        challengeCreateDto.setChallengedId(2l);
        challengeApi.create(challengeCreateDto);

        loginAsDonat2();
        ChallengeActionDto challengeActionDto = new ChallengeActionDto();
        List<ChallengeDto> allChallenges = challengeApi.getAll();
        challengeActionDto.setChallengeId(allChallenges.get(0).getId());
        challengeActionDto.setChallengeAction("ACCEPT");
        challengeApi.answer(challengeActionDto);
        List<ChessGameDto> gamesAfterAccept = gameApi.getAll();

        ChessGameDto chessGameDto = getNewGame(initGames, gamesAfterAccept);
        ChessTableDto chessGame = gameApi.getOneChessGame(chessGameDto.getChessGameId());

        //1. step
        loginBasedOnChessGame(chessGame, Color.WHITE);
        gameApi.chessMove(prepareMove(chessGame, 7, 2, 7, 4, ""));

        //2. step
        loginBasedOnChessGame(chessGame, Color.BLACK);
        chessGame = gameApi.getOneChessGame(chessGameDto.getChessGameId());
        gameApi.chessMove(prepareMove(chessGame, 1, 7, 1, 6, ""));

        //3. step
        loginBasedOnChessGame(chessGame, Color.WHITE);
        chessGame = gameApi.getOneChessGame(chessGameDto.getChessGameId());
        gameApi.chessMove(prepareMove(chessGame, 7, 4, 7, 5, ""));

        //4. step
        loginBasedOnChessGame(chessGame, Color.BLACK);
        chessGame = gameApi.getOneChessGame(chessGameDto.getChessGameId());
        gameApi.chessMove(prepareMove(chessGame, 1, 6, 1, 5, ""));

        //5. step
        loginBasedOnChessGame(chessGame, Color.WHITE);
        chessGame = gameApi.getOneChessGame(chessGameDto.getChessGameId());
        gameApi.chessMove(prepareMove(chessGame, 7, 5, 7, 6, ""));

        //6. step
        loginBasedOnChessGame(chessGame, Color.BLACK);
        chessGame = gameApi.getOneChessGame(chessGameDto.getChessGameId());
        gameApi.chessMove(prepareMove(chessGame, 1, 5, 1, 4, ""));

        //7. step
        loginBasedOnChessGame(chessGame, Color.WHITE);
        chessGame = gameApi.getOneChessGame(chessGameDto.getChessGameId());
        assertEquals(32, chessGame.getFigures().size());
        gameApi.chessMove(prepareMove(chessGame, 7, 6, 8, 7, ""));

        //8. step
        loginBasedOnChessGame(chessGame, Color.BLACK);
        chessGame = gameApi.getOneChessGame(chessGameDto.getChessGameId());
        assertEquals(31, chessGame.getFigures().size());
        gameApi.chessMove(prepareMove(chessGame, 1, 4, 1, 3, ""));

        //9. step
        loginBasedOnChessGame(chessGame, Color.WHITE);
        chessGame = gameApi.getOneChessGame(chessGameDto.getChessGameId());
        ChessTableDto finalChessGame = chessGame;
        shouldFail(()-> gameApi.chessMove(prepareMove(finalChessGame, 8, 7, 7, 8, "PAWN")));
        shouldFail(()-> gameApi.chessMove(prepareMove(finalChessGame, 8, 7, 7, 8, "KING")));
        shouldFail(()-> gameApi.chessMove(prepareMove(finalChessGame, 8, 7, 7, 8, "AAAA")));
        gameApi.chessMove(prepareMove(finalChessGame, 8, 7, 7, 8, "QUEEN"));
        chessGame = gameApi.getOneChessGame(chessGameDto.getChessGameId());

        FigureDto figureDto = giveFigure(chessGame.getFigures(), 7, 8);
        assertNotNull(figureDto);
        assertEquals(figureDto.getFigureType(), ChessFigure.QUEEN);
        assertEquals(figureDto.getColor(), Color.WHITE);

    }


    @Test
    public void drawThreeFoldRepetitionTest() {
        loginAsDonat1();
        List<ChessGameDto> initGames = gameApi.getAll();
        ChallengeCreateDto challengeCreateDto = new ChallengeCreateDto();
        challengeCreateDto.setChallengedId(2l);
        challengeApi.create(challengeCreateDto);

        loginAsDonat2();
        ChallengeActionDto challengeActionDto = new ChallengeActionDto();
        List<ChallengeDto> allChallenges = challengeApi.getAll();
        challengeActionDto.setChallengeId(allChallenges.get(0).getId());
        challengeActionDto.setChallengeAction("ACCEPT");
        challengeApi.answer(challengeActionDto);
        List<ChessGameDto> gamesAfterAccept = gameApi.getAll();

        ChessGameDto chessGameDto = getNewGame(initGames, gamesAfterAccept);
        ChessTableDto chessGame = gameApi.getOneChessGame(chessGameDto.getChessGameId());

        //1. step
        loginBasedOnChessGame(chessGame, Color.WHITE);
        gameApi.chessMove(prepareMove(chessGame, 7, 1, 6, 3, ""));

        //2. step
        loginBasedOnChessGame(chessGame, Color.BLACK);
        chessGame = gameApi.getOneChessGame(chessGameDto.getChessGameId());
        gameApi.chessMove(prepareMove(chessGame, 2, 8, 3, 6, ""));

        //3. step
        loginBasedOnChessGame(chessGame, Color.WHITE);
        chessGame = gameApi.getOneChessGame(chessGameDto.getChessGameId());
        gameApi.chessMove(prepareMove(chessGame, 6, 3, 7, 1, ""));

        //4. step
        loginBasedOnChessGame(chessGame, Color.BLACK);
        chessGame = gameApi.getOneChessGame(chessGameDto.getChessGameId());
        gameApi.chessMove(prepareMove(chessGame, 3, 6, 2, 8, ""));

        //5. step
        loginBasedOnChessGame(chessGame, Color.WHITE);
        chessGame = gameApi.getOneChessGame(chessGameDto.getChessGameId());
        gameApi.chessMove(prepareMove(chessGame, 7, 1, 6, 3, ""));

        //6. step
        loginBasedOnChessGame(chessGame, Color.BLACK);
        chessGame = gameApi.getOneChessGame(chessGameDto.getChessGameId());
        gameApi.chessMove(prepareMove(chessGame, 2, 8, 3, 6, ""));

        //7. step
        loginBasedOnChessGame(chessGame, Color.WHITE);
        chessGame = gameApi.getOneChessGame(chessGameDto.getChessGameId());
        gameApi.chessMove(prepareMove(chessGame, 6, 3, 7, 1, ""));

        //8. step
        loginBasedOnChessGame(chessGame, Color.BLACK);
        chessGame = gameApi.getOneChessGame(chessGameDto.getChessGameId());
        gameApi.chessMove(prepareMove(chessGame, 3, 6, 2, 8, ""));

        //9. step
        loginBasedOnChessGame(chessGame, Color.WHITE);
        chessGame = gameApi.getOneChessGame(chessGameDto.getChessGameId());
        assertEquals(chessGame.getChessGameStatus(), ChessGameStatus.OPEN);
        assertEquals(chessGame.getResult(), Result.OPEN);
        gameApi.chessMove(prepareMove(chessGame, 7, 1, 6, 3, ""));
        chessGame = gameApi.getOneChessGame(chessGameDto.getChessGameId());
        assertEquals(chessGame.getResult(), Result.DRAWN);
        assertEquals(chessGame.getChessGameStatus(), ChessGameStatus.CLOSED);


    }

    @Test
    public void chessMateTest() {
        loginAsDonat1();
        List<ChessGameDto> initGames = gameApi.getAll();
        ChallengeCreateDto challengeCreateDto = new ChallengeCreateDto();
        challengeCreateDto.setChallengedId(2l);
        challengeApi.create(challengeCreateDto);

        loginAsDonat2();
        ChallengeActionDto challengeActionDto = new ChallengeActionDto();
        List<ChallengeDto> allChallenges = challengeApi.getAll();
        challengeActionDto.setChallengeId(allChallenges.get(0).getId());
        challengeActionDto.setChallengeAction("ACCEPT");
        challengeApi.answer(challengeActionDto);
        List<ChessGameDto> gamesAfterAccept = gameApi.getAll();

        ChessGameDto chessGameDto = getNewGame(initGames, gamesAfterAccept);
        ChessTableDto chessGame = gameApi.getOneChessGame(chessGameDto.getChessGameId());

        //1. step
        loginBasedOnChessGame(chessGame, Color.WHITE);
        gameApi.chessMove(prepareMove(chessGame, 6, 2, 6, 3, ""));

        //2. step
        loginBasedOnChessGame(chessGame, Color.BLACK);
        chessGame = gameApi.getOneChessGame(chessGameDto.getChessGameId());
        gameApi.chessMove(prepareMove(chessGame, 5, 7, 5, 6, ""));

        //3. step
        loginBasedOnChessGame(chessGame, Color.WHITE);
        chessGame = gameApi.getOneChessGame(chessGameDto.getChessGameId());
        gameApi.chessMove(prepareMove(chessGame, 7, 2, 7, 4, ""));

        //4. step
        loginBasedOnChessGame(chessGame, Color.BLACK);
        chessGame = gameApi.getOneChessGame(chessGameDto.getChessGameId());
        assertEquals(chessGame.getChessGameStatus(), ChessGameStatus.OPEN);
        assertEquals(chessGame.getResult(), Result.OPEN);
        gameApi.chessMove(prepareMove(chessGame, 4, 8, 8, 4, ""));
        chessGame = gameApi.getOneChessGame(chessGameDto.getChessGameId());
        assertEquals(chessGame.getResult(), Result.WON_USER_TWO);
        assertEquals(chessGame.getChessGameStatus(), ChessGameStatus.CLOSED);

    }

    private FigureDto giveFigure(Set<FigureDto> figures, int x, int y) {
        for (FigureDto figure : figures) {
            if (figure.getCoordX() == x && figure.getCoordY() == y) {
                return figure;
            }
        }
        return null;
    }


    private void loginBasedOnChessGame(ChessTableDto chessGame, Color color) {
        if (color.equals(Color.WHITE)) {
            loginBasedOnUserDto(chessGame.getUserOne());
        } else {
            loginBasedOnUserDto(chessGame.getUserTwo());
        }
    }

    private void loginBasedOnUserDto(UserDto userOne) {
        if (userOne.getId().equals(1l)) {
            loginAsDonat1();
        } else if(userOne.getId().equals(2l)) {
            loginAsDonat2();
        }
    }

    private ChessMoveDto prepareMove(ChessTableDto chessGame, int fromX, int fromY, int toX, int toY, String promotion) {
        ChessMoveDto chessMoveDto = new ChessMoveDto();
        chessMoveDto.setGameId(chessGame.getChessGameId());
        chessMoveDto.setMoveId(chessGame.getLastMoveId() + 1);
        chessMoveDto.setMoveFromX(fromX);
        chessMoveDto.setMoveFromY(fromY);
        chessMoveDto.setMoveToX(toX);
        chessMoveDto.setMoveToY(toY);
        chessMoveDto.setPromoteToFigure(promotion);
        return chessMoveDto;
    }

    private FigureDto getFigureBasedValidMoves(Set<FigureDto> figures, CoordinateDto validMove) {
        for (FigureDto figure : figures) {
            if (figure.getCoordX() == validMove.getFromX() &&
                    figure.getCoordY() == validMove.getFromY()) {
                return figure;
            }
        }
        return null;
    }

    private ChessGameDto getNewGame(List<ChessGameDto> initGames, List<ChessGameDto> gamesAfterAccept) {
        for (ChessGameDto chessGameDto : gamesAfterAccept) {
            if (initGames.stream()
                    .noneMatch(c -> c.getChessGameId().equals(chessGameDto.getChessGameId()))) {
                return chessGameDto;
            }
        }
        return null;
    }

    private boolean theSameFigure(FigureDto figureToFind, Set<FigureDto> figures) {
        for (FigureDto figure : figures) {
            if (sameFigure(figure, figureToFind)) {
                return true;
            }
        }
        return false;
    }

    private boolean sameFigure(FigureDto figure, FigureDto figureToFind) {

        return figure.getColor().equals(figureToFind.getColor()) &&
                figure.getFigureType().equals(figureToFind.getFigureType()) &&
                figure.getCoordX() == figureToFind.getCoordX() &&
                figure.getCoordY() == figureToFind.getCoordY();
    }


}
