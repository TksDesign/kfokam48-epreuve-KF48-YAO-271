package com.kfokam48.presencerelecture.domain.exception;

import org.springframework.http.HttpStatus;

public class EtudiantInconnuException extends ApiException {
    public EtudiantInconnuException() {
        super(HttpStatus.NOT_FOUND, "ETUDIANT_INCONNU", "Cet etudiant n'existe pas.");
    }
}
