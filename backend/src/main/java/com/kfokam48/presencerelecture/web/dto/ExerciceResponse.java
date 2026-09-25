package com.kfokam48.presencerelecture.web.dto;

import com.kfokam48.presencerelecture.domain.Exercice;

/** Reponse de POST /api/exercices, format impose par le contrat. */
public record ExerciceResponse(Long id, String statut) {

    public static ExerciceResponse depuis(Exercice exercice) {
        return new ExerciceResponse(exercice.getId(), exercice.getStatut().name());
    }
}
