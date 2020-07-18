package com.donat.donchess;

import com.donat.donchess.dto.challange.ChallengeActionDto;
import com.donat.donchess.dto.challange.ChallengeCreateDto;
import com.donat.donchess.dto.challange.ChallengeDto;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;

public class ChallengeApiOperation {

    private RestTemplate restTemplate;
    private String endpointUrl;

    public ChallengeApiOperation(RestTemplate restTemplate, String endpointUrl) {
        this.restTemplate = restTemplate;
        this.endpointUrl = endpointUrl;
    }

    public List<ChallengeDto> getAll() {
        ResponseEntity<List<ChallengeDto>> result = restTemplate.exchange(
                endpointUrl + "/list",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<ChallengeDto>>() {
                });

        return result.getBody();
    }

    public ResponseEntity create(ChallengeCreateDto challengeCreateDto) {

        ResponseEntity result = restTemplate.postForObject(
                endpointUrl + "/create",
                challengeCreateDto,
                ResponseEntity.class);

        return result;
    }

    public void answer(ChallengeActionDto challengeActionDto) {

        restTemplate.postForObject(
                endpointUrl + "/answer",
                challengeActionDto,
                ResponseEntity.class);
    }

}
