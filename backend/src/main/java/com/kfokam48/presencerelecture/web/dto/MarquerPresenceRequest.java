package com.kfokam48.presencerelecture.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/** Corps de POST /api/presences, tel qu'impose par le contrat. */
public record MarquerPresenceRequest(
        @NotBlank(message = "le code est obligatoire") String code,
        @NotNull(message = "etudiantId est obligatoire") Long etudiantId
) {
}
