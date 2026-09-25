package com.kfokam48.presencerelecture.domain.exception;

import org.springframework.http.HttpStatus;

/**
 * Base des exceptions metier mappees vers le format d'erreur du contrat
 * (voir GlobalExceptionHandler). Chaque sous-classe correspond a un code
 * d'erreur fixe enumere dans api/contrat.yaml.
 */
public abstract class ApiException extends RuntimeException {

    private final HttpStatus httpStatus;
    private final String code;

    protected ApiException(HttpStatus httpStatus, String code, String message) {
        super(message);
        this.httpStatus = httpStatus;
        this.code = code;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getCode() {
        return code;
    }
}
