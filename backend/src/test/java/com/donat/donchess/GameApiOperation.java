package com.donat.donchess;

import com.donat.donchess.dto.chessGame.ChessGameDto;
import com.donat.donchess.dto.chessGame.ChessMoveDto;
import com.donat.donchess.dto.chessGame.ChessTableDto;
import com.donat.donchess.dto.chessGame.ResultDto;
import com.donat.donchess.dto.chessGame.ValidMovesDto;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;

public class GameApiOperation {

    private RestTemplate restTemplate;
    private String endpointUrl;

    public GameApiOperation(RestTemplate restTemplate, String endpointUrl) {
        this.restTemplate = restTemplate;
        this.endpointUrl = endpointUrl;
    }

    public List<ChessGameDto> getAll() {
        ResponseEntity<List<ChessGameDto>> result = restTemplate.exchange(
                endpointUrl,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<ChessGameDto>>() {
                });

        return result.getBody();
    }

    public ChessTableDto getOneChessGame(long id) {
        ResponseEntity<ChessTableDto> result = restTemplate.exchange(
                endpointUrl + "/" + id,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<ChessTableDto>() {
                });

        return result.getBody();
    }

    public ValidMovesDto getValidMoves(long id) {
        ResponseEntity<ValidMovesDto> result = restTemplate.exchange(
                endpointUrl + "/validMoves/" + id,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<ValidMovesDto>() {
                });

        return result.getBody();
    }

    public ResultDto chessMove(ChessMoveDto chessMoveDto) {
        ResultDto resultDto = restTemplate.postForObject(
                endpointUrl + "/move",
                chessMoveDto,
               ResultDto.class);
        return resultDto;
    }

}
