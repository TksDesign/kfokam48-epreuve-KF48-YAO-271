package com.kfokam48.presencerelecture.domain.exception;

import org.springframework.http.HttpStatus;

/**
 * RG2 : au bout de 5 codes inconnus, l'etudiant est bloque 2 minutes.
 * Extension au contrat impose, documentee dans api/contrat.yaml.
 */
public class TropDeTentativesException extends ApiException {
    public TropDeTentativesException() {
        super(HttpStatus.TOO_MANY_REQUESTS, "TROP_DE_TENTATIVES",
                "Trop de codes incorrects. Reessayez dans 2 minutes.");
    }
}
