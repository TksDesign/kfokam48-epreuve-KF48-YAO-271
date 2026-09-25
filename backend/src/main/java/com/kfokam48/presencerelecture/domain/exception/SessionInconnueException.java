package com.kfokam48.presencerelecture.domain.exception;

import org.springframework.http.HttpStatus;

public class SessionInconnueException extends ApiException {
    public SessionInconnueException() {
        super(HttpStatus.NOT_FOUND, "SESSION_INCONNUE", "Cette session n'existe pas.");
    }
}
