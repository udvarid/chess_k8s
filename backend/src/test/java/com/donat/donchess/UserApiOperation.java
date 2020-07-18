package com.donat.donchess;

import com.donat.donchess.dto.User.RegisterDto;
import com.donat.donchess.dto.User.UserDto;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;

public class UserApiOperation {

    private RestTemplate restTemplate;
    private String endpointUrl;

    public UserApiOperation(RestTemplate restTemplate, String endpointUrl) {
        this.restTemplate = restTemplate;
        this.endpointUrl = endpointUrl;
    }

    public List<UserDto> getAll() {

        ResponseEntity<List<UserDto>> result = restTemplate.exchange(
                endpointUrl + "/listOfUsers",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<UserDto>>() {
                });

        return result.getBody();
    }

    public void register(RegisterDto registerDto) {

        restTemplate.postForObject(
                endpointUrl + "/register",
                registerDto,
                ResponseEntity.class);
    }

    public void confirmRegistration(String token) {
        restTemplate.exchange(
                endpointUrl + "/confirmation?token=" + token,
                HttpMethod.GET,
                null,
                ResponseEntity.class);
    }

}
