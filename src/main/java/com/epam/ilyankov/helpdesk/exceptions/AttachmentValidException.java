package com.epam.ilyankov.helpdesk.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class AttachmentValidException extends RuntimeException {

    public AttachmentValidException(String message) {
        super(message);
    }
}
