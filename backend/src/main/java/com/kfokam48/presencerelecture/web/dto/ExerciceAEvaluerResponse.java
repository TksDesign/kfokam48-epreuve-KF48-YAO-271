package com.kfokam48.presencerelecture.web.dto;

import com.kfokam48.presencerelecture.domain.Exercice;

/** Un exercice assigne a un relecteur, pas encore evalue - pour peupler le picker frontend. */
public record ExerciceAEvaluerResponse(Long id, String lien) {

    public static ExerciceAEvaluerResponse depuis(Exercice exercice) {
        return new ExerciceAEvaluerResponse(exercice.getId(), exercice.getLien());
    }
}
