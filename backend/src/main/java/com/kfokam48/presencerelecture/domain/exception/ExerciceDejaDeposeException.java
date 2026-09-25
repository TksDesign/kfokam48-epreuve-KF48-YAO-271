package com.kfokam48.presencerelecture.domain.exception;

import org.springframework.http.HttpStatus;

public class ExerciceDejaDeposeException extends ApiException {
    public ExerciceDejaDeposeException() {
        super(HttpStatus.CONFLICT, "EXERCICE_DEJA_DEPOSE", "Un exercice a deja ete depose pour cette session.");
    }
}
