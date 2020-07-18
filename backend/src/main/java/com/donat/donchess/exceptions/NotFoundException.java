package com.donat.donchess.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class NotFoundException extends RuntimeException{

    public NotFoundException(String message, Long id) {
        super(message + " " + id);
    }

    public NotFoundException(String message) {
        super(message);
    }

}
