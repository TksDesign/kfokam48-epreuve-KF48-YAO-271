package com.kfokam48.presencerelecture.service;

import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * RG2 : au bout de 5 codes inconnus, l'etudiant est bloque 2 minutes (Q4).
 */
class TentativesCodeTrackerTest {

    /** Horloge controlable pour avancer le temps sans attendre reellement. */
    static class HorlogeControlable extends Clock {
        private Instant instant;

        HorlogeControlable(Instant instant) {
            this.instant = instant;
        }

        void avancer(Duration duree) {
            instant = instant.plus(duree);
        }

        @Override
        public ZoneId getZone() {
            return ZoneId.of("UTC");
        }

        @Override
        public Clock withZone(ZoneId zone) {
            return this;
        }

        @Override
        public Instant instant() {
            return instant;
        }
    }

    @Test
    void n_est_pas_bloque_avant_5_echecs() {
        var horloge = new HorlogeControlable(Instant.parse("2026-09-25T10:00:00Z"));
        var tracker = new TentativesCodeTracker(horloge);

        for (int i = 0; i < 4; i++) {
            tracker.enregistrerEchec(1L);
        }

        assertThat(tracker.estBloque(1L)).isFalse();
    }

    @Test
    void est_bloque_au_5e_echec_et_libere_apres_2_minutes() {
        var horloge = new HorlogeControlable(Instant.parse("2026-09-25T10:00:00Z"));
        var tracker = new TentativesCodeTracker(horloge);

        for (int i = 0; i < 5; i++) {
            tracker.enregistrerEchec(1L);
        }
        assertThat(tracker.estBloque(1L)).isTrue();

        horloge.avancer(Duration.ofMinutes(2).plusSeconds(1));
        assertThat(tracker.estBloque(1L)).isFalse();
    }

    @Test
    void reinitialiser_efface_le_compteur() {
        var horloge = new HorlogeControlable(Instant.parse("2026-09-25T10:00:00Z"));
        var tracker = new TentativesCodeTracker(horloge);

        for (int i = 0; i < 5; i++) {
            tracker.enregistrerEchec(2L);
        }
        tracker.reinitialiser(2L);

        assertThat(tracker.estBloque(2L)).isFalse();
    }

    @Test
    void chaque_etudiant_a_son_propre_compteur() {
        var horloge = new HorlogeControlable(Instant.parse("2026-09-25T10:00:00Z"));
        var tracker = new TentativesCodeTracker(horloge);

        for (int i = 0; i < 5; i++) {
            tracker.enregistrerEchec(1L);
        }

        assertThat(tracker.estBloque(1L)).isTrue();
        assertThat(tracker.estBloque(2L)).isFalse();
    }
}
