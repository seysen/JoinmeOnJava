package com.dataart.joinme.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
public class EmptyFieldsException extends RuntimeException {
    public EmptyFieldsException() {
        super("All required fields must be filled");
    }
}
