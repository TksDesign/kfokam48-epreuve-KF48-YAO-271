package com.kfokam48.presencerelecture.web.dto;

import com.kfokam48.presencerelecture.domain.Session;

import java.time.Instant;

/** Reponse de POST /api/sessions, format impose par le contrat. */
public record SessionResponse(Long id, String code, Instant ouvertureAt, Instant expirationAt) {

    public static SessionResponse depuis(Session session) {
        return new SessionResponse(session.getId(), session.getCode(), session.getOuvertureAt(), session.getExpirationAt());
    }
}
