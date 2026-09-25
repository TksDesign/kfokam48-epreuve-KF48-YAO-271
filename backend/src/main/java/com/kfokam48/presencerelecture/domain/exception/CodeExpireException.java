package com.kfokam48.presencerelecture.domain.exception;

import org.springframework.http.HttpStatus;

public class CodeExpireException extends ApiException {
    public CodeExpireException() {
        super(HttpStatus.GONE, "CODE_EXPIRE", "Le code de presence a expire.");
    }
}
