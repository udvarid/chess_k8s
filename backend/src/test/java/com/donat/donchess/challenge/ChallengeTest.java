package com.donat.donchess.challenge;

import com.donat.donchess.AbstractApiTest;
import com.donat.donchess.domain.*;
import com.donat.donchess.dto.challange.ChallengeActionDto;
import com.donat.donchess.dto.challange.ChallengeCreateDto;
import com.donat.donchess.dto.challange.ChallengeDto;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.inject.Provider;
import javax.persistence.EntityManager;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class ChallengeTest extends AbstractApiTest {

    @Autowired
    private Provider<EntityManager> entityManager;

    @Test
    public void createNewChallenge() {
        JPAQueryFactory query = new JPAQueryFactory(entityManager);
        QChallenge challengeFromQ = QChallenge.challenge;

        ChallengeCreateDto challengeCreateDto = new ChallengeCreateDto();
        challengeCreateDto.setChallengedId(2l);

        Challenge challenge = query
                .selectFrom(challengeFromQ)
                .where(challengeFromQ.challenger.id.eq(1l)
                        .and(challengeFromQ.challenged.id.eq(2l)))
                .fetchOne();

        assertNull(challenge);

        List<ChallengeDto> challengesCurrent = challengeApi.getAll();

        loginAsDonat1();
        challengeApi.create(challengeCreateDto);

        Challenge challenge2 = query
                .selectFrom(challengeFromQ)
                .where(challengeFromQ.challenger.id.eq(1l)
                        .and(challengeFromQ.challenged.id.eq(2l)))
                .fetchOne();

        assertNotNull(challenge2);
        assertTrue(challenge2.getChallenger().getId().equals(1l));
        assertTrue(challenge2.getChallenged().getId().equals(2l));

        List<ChallengeDto> challengesAfter = challengeApi.getAll();

        assertTrue(challengesCurrent.size() + 1 == challengesAfter.size());

        ChallengeActionDto challengeActionDto = new ChallengeActionDto();
        challengeActionDto.setChallengeId(challenge2.getId());
        challengeActionDto.setChallengeAction("DELETE");

        challengeApi.answer(challengeActionDto);
    }

    @Test
    public void sameChallengerIdAndChallengedIdDeniedTest() {
        JPAQueryFactory query = new JPAQueryFactory(entityManager);
        QChallenge challengeFromQ = QChallenge.challenge;
        loginAsDonat1();
        ChallengeCreateDto challengeCreateDto = new ChallengeCreateDto();
        challengeCreateDto.setChallengedId(1l);
        shouldFail(() -> challengeApi.create(challengeCreateDto));
    }

    @Test
    public void notValidChallengedIdTest() {
        JPAQueryFactory query = new JPAQueryFactory(entityManager);
        QChallenge challengeFromQ = QChallenge.challenge;
        loginAsDonat1();
        ChallengeCreateDto challengeCreateDto = new ChallengeCreateDto();
        challengeCreateDto.setChallengedId(10000l);
        shouldFail(() -> challengeApi.create(challengeCreateDto));
    }

    @Test
    public void duplicatedChallengeIsDeniedTest() {
        JPAQueryFactory query = new JPAQueryFactory(entityManager);
        QChallenge challengeFromQ = QChallenge.challenge;
        loginAsDonat1();
        ChallengeCreateDto challengeCreateDto = new ChallengeCreateDto();
        challengeCreateDto.setChallengedId(2l);
        challengeApi.create(challengeCreateDto);

        ChallengeCreateDto challengeCreateDto2 = new ChallengeCreateDto();
        challengeCreateDto2.setChallengedId(2l);
        shouldFail(() -> challengeApi.create(challengeCreateDto2));

        Challenge challenge = query
                .selectFrom(challengeFromQ)
                .where(challengeFromQ.challenger.id.eq(1l)
                        .and(challengeFromQ.challenged.id.eq(2l)))
                .fetchOne();

        ChallengeActionDto challengeActionDto = new ChallengeActionDto();
        challengeActionDto.setChallengeId(challenge.getId());
        challengeActionDto.setChallengeAction("DELETE");

        challengeApi.answer(challengeActionDto);
    }

    @Test
    public void notValidChallengeIdTest() {
        JPAQueryFactory query = new JPAQueryFactory(entityManager);
        QChallenge challengeFromQ = QChallenge.challenge;
        loginAsDonat1();

        List<Challenge> challenges = query.selectFrom(challengeFromQ)
                .orderBy(challengeFromQ.id.desc())
                .fetch();

        Long idMax = challenges.size() == 0 ? 1l : challenges.get(0).getId() + 1l;

        System.out.println(idMax);

        ChallengeCreateDto challengeCreateDto = new ChallengeCreateDto();
        challengeCreateDto.setChallengedId(idMax);
        shouldFail(() -> challengeApi.create(challengeCreateDto));
    }

    @Test
    public void invalidActionsDeniedTest() {
        JPAQueryFactory query = new JPAQueryFactory(entityManager);
        QChallenge challengeFromQ = QChallenge.challenge;
        loginAsDonat1();
        ChallengeCreateDto challengeCreateDto = new ChallengeCreateDto();
        challengeCreateDto.setChallengedId(2l);
        challengeApi.create(challengeCreateDto);

        Challenge challenge = query
                .selectFrom(challengeFromQ)
                .where(challengeFromQ.challenger.id.eq(1l)
                        .and(challengeFromQ.challenged.id.eq(2l)))
                .fetchOne();

        ChallengeActionDto challengeActionDto = new ChallengeActionDto();
        challengeActionDto.setChallengeId(challenge.getId());

        challengeActionDto.setChallengeAction("maki");
        shouldFail(() -> challengeApi.answer(challengeActionDto));

        challengeActionDto.setChallengeAction("delete");
        shouldFail(() -> challengeApi.answer(challengeActionDto));

        challengeActionDto.setChallengeAction("DELETE");
        challengeApi.answer(challengeActionDto);

    }

    @Test
    public void onlyTheOnwerOfChallengeCanDeleteTest() {
        JPAQueryFactory query = new JPAQueryFactory(entityManager);
        QChallenge challengeFromQ = QChallenge.challenge;
        loginAsDonat1();
        ChallengeCreateDto challengeCreateDto = new ChallengeCreateDto();
        challengeCreateDto.setChallengedId(2l);
        challengeApi.create(challengeCreateDto);

        Challenge challenge = query
                .selectFrom(challengeFromQ)
                .where(challengeFromQ.challenger.id.eq(1l)
                        .and(challengeFromQ.challenged.id.eq(2l)))
                .fetchOne();

        ChallengeActionDto challengeActionDto = new ChallengeActionDto();
        challengeActionDto.setChallengeId(challenge.getId());
        challengeActionDto.setChallengeAction("DELETE");

        loginAsDonat2();
        shouldFail(() -> challengeApi.answer(challengeActionDto));

        loginAsDonat1();
        challengeApi.answer(challengeActionDto);
    }

    @Test
    public void ownerOfChallengeCanOnlyDeleteTest() {
        JPAQueryFactory query = new JPAQueryFactory(entityManager);
        QChallenge challengeFromQ = QChallenge.challenge;
        loginAsDonat1();
        ChallengeCreateDto challengeCreateDto = new ChallengeCreateDto();
        challengeCreateDto.setChallengedId(2l);
        challengeApi.create(challengeCreateDto);

        Challenge challenge = query
                .selectFrom(challengeFromQ)
                .where(challengeFromQ.challenger.id.eq(1l)
                        .and(challengeFromQ.challenged.id.eq(2l)))
                .fetchOne();

        ChallengeActionDto challengeActionDto = new ChallengeActionDto();
        challengeActionDto.setChallengeId(challenge.getId());

        challengeActionDto.setChallengeAction("ACCEPT");
        shouldFail(() -> challengeApi.answer(challengeActionDto));

        challengeActionDto.setChallengeAction("DECLINE");
        shouldFail(() -> challengeApi.answer(challengeActionDto));

        challengeActionDto.setChallengeAction("DELETE");
        challengeApi.answer(challengeActionDto);
    }

    @Test
    public void onlyChallengedCanDeclineTest() {
        JPAQueryFactory query = new JPAQueryFactory(entityManager);
        QChallenge challengeFromQ = QChallenge.challenge;
        loginAsDonat1();
        ChallengeCreateDto challengeCreateDto = new ChallengeCreateDto();
        challengeCreateDto.setChallengedId(2l);
        challengeApi.create(challengeCreateDto);

        Challenge challenge = query
                .selectFrom(challengeFromQ)
                .where(challengeFromQ.challenger.id.eq(1l)
                        .and(challengeFromQ.challenged.id.eq(2l)))
                .fetchOne();

        assertNotNull(challenge);

        ChallengeActionDto challengeActionDto = new ChallengeActionDto();
        challengeActionDto.setChallengeId(challenge.getId());
        challengeActionDto.setChallengeAction("DECLINE");

        loginAsDonat2();
        challengeApi.answer(challengeActionDto);
        Challenge challengeAfterDecline = query
                .selectFrom(challengeFromQ)
                .where(challengeFromQ.challenger.id.eq(1l)
                        .and(challengeFromQ.challenged.id.eq(2l)))
                .fetchOne();

        assertNull(challengeAfterDecline);

    }

    @Test
    public void onlyChallengedCanAcceptTest() {
        JPAQueryFactory query = new JPAQueryFactory(entityManager);
        QChallenge challengeFromQ = QChallenge.challenge;

        loginAsDonat1();
        ChallengeCreateDto challengeCreateDto = new ChallengeCreateDto();
        challengeCreateDto.setChallengedId(2l);
        challengeApi.create(challengeCreateDto);

        Challenge challenge = query
                .selectFrom(challengeFromQ)
                .where(challengeFromQ.challenger.id.eq(1l)
                        .and(challengeFromQ.challenged.id.eq(2l)))
                .fetchOne();

        assertNotNull(challenge);

        ChallengeActionDto challengeActionDto = new ChallengeActionDto();
        challengeActionDto.setChallengeId(challenge.getId());
        challengeActionDto.setChallengeAction("ACCEPT");

        int numberOfGamesBeforeAccept =
                countGamesBetweenPlayers(challenge.getChallenger(), challenge.getChallenged());

        loginAsDonat2();
        challengeApi.answer(challengeActionDto);
        Challenge challengeAfterDecline = query
                .selectFrom(challengeFromQ)
                .where(challengeFromQ.challenger.id.eq(1l)
                        .and(challengeFromQ.challenged.id.eq(2l)))
                .fetchOne();

        assertNull(challengeAfterDecline);

        int numberOfGamesAfterAccept =
                countGamesBetweenPlayers(challenge.getChallenger(), challenge.getChallenged());

        assertTrue(numberOfGamesBeforeAccept + 1 == numberOfGamesAfterAccept);


    }


    @Test
    public void createMoreChallengeTest() {

        JPAQueryFactory query = new JPAQueryFactory(entityManager);
        QChallenge challengeFromQ = QChallenge.challenge;

        loginAsDonat1();
        ChallengeCreateDto challengeCreateDto = new ChallengeCreateDto();
        challengeCreateDto.setChallengedId(2l);
        challengeApi.create(challengeCreateDto);

        Challenge challengeOne = query
                .selectFrom(challengeFromQ)
                .where(challengeFromQ.challenger.id.eq(1l)
                        .and(challengeFromQ.challenged.id.eq(2l)))
                .fetchOne();

        assertNotNull(challengeOne);

        ChallengeActionDto challengeActionDto = new ChallengeActionDto();
        challengeActionDto.setChallengeId(challengeOne.getId());
        challengeActionDto.setChallengeAction("DELETE");

        ChallengeCreateDto challengeCreateDto2 = new ChallengeCreateDto();
        challengeApi.create(challengeCreateDto2);

        Challenge challengeTwo = query
                .selectFrom(challengeFromQ)
                .where(challengeFromQ.challenger.id.eq(1l)
                        .and(challengeFromQ.challenged.id.isNull()))
                .fetchOne();

        assertNotNull(challengeTwo);

        ChallengeActionDto challengeActionDto2 = new ChallengeActionDto();
        challengeActionDto2.setChallengeId(challengeTwo.getId());
        challengeActionDto2.setChallengeAction("DELETE");

        challengeApi.answer(challengeActionDto);
        challengeApi.answer(challengeActionDto2);

    }

    private int countGamesBetweenPlayers(User challenger, User challenged) {
        JPAQueryFactory query = new JPAQueryFactory(entityManager);
        QChessGame chessGameFromQ = QChessGame.chessGame;

        List<ChessGame> chessGames = query
                .selectFrom(chessGameFromQ)
                .fetch();

        List<ChessGame> filteredChessgames = chessGames
                .stream()
                .filter(game ->
                        game.getUserOne().getId().equals(challenger.getId()) &&
                                game.getUserTwo().getId().equals(challenged.getId()) ||
                                game.getUserOne().getId().equals(challenged.getId()) &&
                                        game.getUserTwo().getId().equals(challenger.getId())
                ).collect(Collectors.toList());

        return filteredChessgames.size();
    }

}

