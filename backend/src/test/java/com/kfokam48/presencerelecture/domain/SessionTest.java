package com.kfokam48.presencerelecture.domain;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * RG1 : le code de presence expire 15 minutes apres l'ouverture de la session.
 */
class SessionTest {

    @Test
    void le_code_est_valide_avant_15_minutes() {
        Instant ouverture = Instant.parse("2026-09-25T10:00:00Z");
        Session session = new Session("Cours", "ABC123", ouverture, ouverture.plus(15, ChronoUnit.MINUTES), 1L);

        assertThat(session.estExpiree(ouverture.plus(14, ChronoUnit.MINUTES))).isFalse();
    }

    @Test
    void le_code_est_expire_apres_15_minutes() {
        Instant ouverture = Instant.parse("2026-09-25T10:00:00Z");
        Session session = new Session("Cours", "ABC123", ouverture, ouverture.plus(15, ChronoUnit.MINUTES), 1L);

        assertThat(session.estExpiree(ouverture.plus(15, ChronoUnit.MINUTES).plusSeconds(1))).isTrue();
    }
}
