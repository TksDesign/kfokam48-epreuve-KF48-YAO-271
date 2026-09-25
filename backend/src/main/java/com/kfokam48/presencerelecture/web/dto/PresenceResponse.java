package com.kfokam48.presencerelecture.web.dto;

import com.kfokam48.presencerelecture.domain.Presence;

/** Reponse de POST /api/presences et POST /api/sessions/{id}/presences-manuelles. */
public record PresenceResponse(Long id, Long sessionId, Long etudiantId, String source) {

    public static PresenceResponse depuis(Presence presence) {
        return new PresenceResponse(presence.getId(), presence.getSessionId(), presence.getEtudiantId(),
                presence.getSource().name());
    }
}
