package com.epam.ilyankov.helpdesk.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class FeedbackExistException extends RuntimeException{

    public FeedbackExistException(String message) {
        super(message);
    }
}
