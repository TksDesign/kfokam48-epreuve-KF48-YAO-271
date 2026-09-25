package com.kfokam48.presencerelecture.domain.exception;

import org.springframework.http.HttpStatus;

public class RelectureDejaRenduException extends ApiException {
    public RelectureDejaRenduException() {
        super(HttpStatus.CONFLICT, "RELECTURE_DEJA_RENDUE", "Cette relecture a deja ete rendue et est definitive.");
    }
}
