package com.donat.donchess;

import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.Before;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;


import static org.junit.Assert.*;

public abstract class AbstractApiTest extends AncestorAbstract {

    private static final String BASE_URL = "http://localhost";
    private static final String USER_ENDPOINT = "/api/user";
    private static final String CHALLENGE_ENDPOINT = "/api/challenge";
    private static final String CHESSGAME_ENDPOINT = "/api/game";


    protected RestTemplate restTemplate;
    protected UserApiOperation userApi;
    protected ChallengeApiOperation challengeApi;
    protected GameApiOperation gameApi;

    @LocalServerPort
    private int port;

    private String getBaseUrl() {
        return BASE_URL + ":" + port;
    }

    @Before
    public void init() {
        restTemplate = getRestTemplate();
        userApi = new UserApiOperation(restTemplate, getBaseUrl() + USER_ENDPOINT);
        challengeApi = new ChallengeApiOperation(restTemplate, getBaseUrl() + CHALLENGE_ENDPOINT);
        gameApi = new GameApiOperation(restTemplate, getBaseUrl() + CHESSGAME_ENDPOINT);
    }

    protected void loginAsDonat1() {
        login("udvarid@hotmail.com", "1234");
    }
    protected void loginAsDonat2() {
        login("udvari.donat@gmail.com", "1234");
    }


    protected void login(String username, String password) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("username", username);
        formData.add("password", password);

        restTemplate.postForEntity(getBaseUrl() + "/login", new HttpEntity<>(formData, headers), String.class);

    }

    protected void shouldFail(Runnable runnable) {
        shouldFail(runnable, null);
    }

    protected void shouldFail(Runnable runnable, Class<? extends Exception> expectedClass) {
        try {
            runnable.run();
            fail("Should have been failed");
        } catch (Exception exception) {
            if (expectedClass != null) {
                assertEquals(expectedClass, exception.getClass());
            }
        }
    }


    private RestTemplate getRestTemplate() {
        CloseableHttpClient httpclient = HttpClients.custom()
                .setDefaultCookieStore(new BasicCookieStore())
                .build();

        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpclient);

        return new RestTemplate(requestFactory);
    }
}
