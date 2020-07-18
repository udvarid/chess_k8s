package com.donat.donchess.user;

import com.donat.donchess.AbstractApiTest;
import com.donat.donchess.domain.QUser;
import com.donat.donchess.domain.User;
import com.donat.donchess.dto.User.RegisterDto;
import com.donat.donchess.dto.User.UserDto;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.inject.Provider;
import javax.persistence.EntityManager;
import java.util.List;

import static org.junit.Assert.*;

public class UserTest extends AbstractApiTest {

    public static final String MAIL_FOR_REGISTRATION = "donat.udvari@ponte.hu";

    @Autowired
    private Provider<EntityManager> entityManager;

    @Test
    public void createUserTest() {
        JPAQueryFactory query = new JPAQueryFactory(entityManager);
        QUser userFromQ = QUser.user;
        List<UserDto> userDtos = userApi.getAll();
        List<User> users = query
                .selectFrom(userFromQ)
                .where(userFromQ.enabled.eq(true))
                .fetch();

        assertEquals(userDtos.size(), users.size());
        users.forEach(user -> {
            assertTrue(userDtos
                    .stream()
                    .anyMatch(userDto -> userDto.getId().equals(user.getId())));
        });
    }

    @Test
    public void registerUserWithConfirmationTest() {
        JPAQueryFactory query = new JPAQueryFactory(entityManager);
        QUser userFromQ = QUser.user;

        RegisterDto registerDto = new RegisterDto();
        registerDto.setEmail(MAIL_FOR_REGISTRATION);
        registerDto.setFullName("Udvari Donát - test");
        registerDto.setPassword("1234");

        userApi.register(registerDto);

        //we can found in the database
        User unconfirmedUser = query.selectFrom(userFromQ)
                .where(userFromQ.email.eq(MAIL_FOR_REGISTRATION))
                .fetchOne();
        assertNotNull(unconfirmedUser);

        //the API doesn't give this user as this isn't confirmed yet
        List<UserDto> confirmedUsersFromApiBeforeConfirmation = userApi.getAll();
        assertFalse(confirmedUsersFromApiBeforeConfirmation
                .stream()
                .anyMatch(user -> user.getFullName().equals(registerDto.getFullName())));


        userApi.confirmRegistration(unconfirmedUser.getAuthenticationToken());

        //the API gives this user as this isn confirmed at this moment
        List<UserDto> confirmedUsersFromApiAfterConfirmation = userApi.getAll();
        assertTrue(confirmedUsersFromApiAfterConfirmation
                .stream()
                .anyMatch(user -> user.getFullName().equals(registerDto.getFullName())));




    }








}
