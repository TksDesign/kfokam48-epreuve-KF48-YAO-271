package com.kfokam48.presencerelecture.domain.exception;

import org.springframework.http.HttpStatus;

public class AutoRelectureException extends ApiException {
    public AutoRelectureException() {
        super(HttpStatus.FORBIDDEN, "AUTO_RELECTURE", "Un etudiant ne peut pas relire son propre exercice.");
    }
}
