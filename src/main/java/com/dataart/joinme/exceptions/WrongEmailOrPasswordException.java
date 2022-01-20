package com.dataart.joinme.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class WrongEmailOrPasswordException extends RuntimeException {
    public WrongEmailOrPasswordException() {
        super("Invalid email or password");
    }
}
