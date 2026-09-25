package com.kfokam48.presencerelecture.domain.exception;

import org.springframework.http.HttpStatus;

public class ExerciceInconnuException extends ApiException {
    public ExerciceInconnuException() {
        super(HttpStatus.NOT_FOUND, "EXERCICE_INCONNU", "Cet exercice n'existe pas.");
    }
}
