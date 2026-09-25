package com.kfokam48.presencerelecture.domain.exception;

import org.springframework.http.HttpStatus;

public class DejaPresentException extends ApiException {
    public DejaPresentException() {
        super(HttpStatus.CONFLICT, "DEJA_PRESENT", "Cet etudiant est deja marque present pour cette session.");
    }
}
