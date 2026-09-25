package com.kfokam48.presencerelecture.domain.exception;

import org.springframework.http.HttpStatus;

public class CodeInconnuException extends ApiException {
    public CodeInconnuException() {
        super(HttpStatus.BAD_REQUEST, "CODE_INCONNU", "Ce code de presence n'existe pas.");
    }
}
