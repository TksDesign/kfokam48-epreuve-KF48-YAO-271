package com.kfokam48.presencerelecture.web.dto;

import jakarta.validation.constraints.NotNull;

/** Corps de POST /api/sessions/{id}/presences-manuelles (issue #4, EF7/RG8). */
public record PresenceManuelleRequest(
        @NotNull(message = "etudiantId est obligatoire") Long etudiantId
) {
}
