package com.kfokam48.presencerelecture.domain.exception;

import org.springframework.http.HttpStatus;

public class LienInvalideException extends ApiException {
    public LienInvalideException() {
        super(HttpStatus.BAD_REQUEST, "LIEN_INVALIDE", "Le lien fourni n'est pas une URL valide.");
    }
}
