package com.kfokam48.presencerelecture.domain.exception;

import org.springframework.http.HttpStatus;

public class NoteInvalideException extends ApiException {
    public NoteInvalideException() {
        super(HttpStatus.BAD_REQUEST, "NOTE_INVALIDE", "La note doit etre un nombre entier compris entre 0 et 20.");
    }
}
