package com.kfokam48.presencerelecture.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Corps de POST /api/relectures/{id}. `relecteurId` ajoute au contrat impose
 * (voir api/contrat.yaml et docs/CAHIER_DES_CHARGES.md §7).
 */
public record RendreRelectureRequest(
        @NotNull(message = "la note est obligatoire") Integer note,
        @NotBlank(message = "le commentaire est obligatoire") String commentaire,
        @NotNull(message = "relecteurId est obligatoire") Long relecteurId
) {
}
