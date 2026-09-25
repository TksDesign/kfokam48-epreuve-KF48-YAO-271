package com.kfokam48.presencerelecture.domain.exception;

import org.springframework.http.HttpStatus;

public class DejaClotureeException extends ApiException {
    public DejaClotureeException() {
        super(HttpStatus.CONFLICT, "DEJA_CLOTUREE", "Cette session est deja cloturee.");
    }
}
