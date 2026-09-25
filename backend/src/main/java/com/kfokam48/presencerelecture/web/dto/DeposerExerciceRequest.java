package com.kfokam48.presencerelecture.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/** Corps de POST /api/exercices, tel qu'impose par le contrat. */
public record DeposerExerciceRequest(
        @NotNull(message = "sessionId est obligatoire") Long sessionId,
        @NotNull(message = "etudiantId est obligatoire") Long etudiantId,
        @NotBlank(message = "le lien est obligatoire") String lien
) {
}
