package com.kfokam48.presencerelecture.domain.exception;

import org.springframework.http.HttpStatus;

public class PromotionInconnueException extends ApiException {
    public PromotionInconnueException() {
        super(HttpStatus.NOT_FOUND, "PROMOTION_INCONNUE", "Cette promotion n'existe pas.");
    }
}
