package com.kfokam48.presencerelecture.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/** Corps de POST /api/sessions, tel qu'impose par le contrat. */
public record OuvrirSessionRequest(
        @NotBlank(message = "le titre est obligatoire") String titre,
        @NotNull(message = "promotionId est obligatoire") Long promotionId
) {
}
