package com.kfokam48.presencerelecture.service;

import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * RG2 : au bout de 5 codes inconnus saisis par un etudiant, il est bloque
 * 2 minutes ("sinon ils vont deviner les codes entre eux", Q4).
 *
 * Suivi en memoire, par etudiant : suffisant pour la portee de l'epreuve
 * (un seul processus backend). Une vraie mise en prod partagerait cet etat
 * (Redis, table dediee) si plusieurs instances tournaient en parallele.
 */
@Component
public class TentativesCodeTracker {

    static final int SEUIL_BLOCAGE = 5;
    static final Duration DUREE_BLOCAGE = Duration.ofMinutes(2);

    private record Etat(int echecs, Instant bloqueJusqua) {
    }

    private final Map<Long, Etat> etatsParEtudiant = new ConcurrentHashMap<>();
    private final Clock clock;

    public TentativesCodeTracker(Clock clock) {
        this.clock = clock;
    }

    public boolean estBloque(Long etudiantId) {
        Etat etat = etatsParEtudiant.get(etudiantId);
        return etat != null && etat.bloqueJusqua() != null && clock.instant().isBefore(etat.bloqueJusqua());
    }

    public void enregistrerEchec(Long etudiantId) {
        etatsParEtudiant.compute(etudiantId, (id, etat) -> {
            int echecs = (etat == null ? 0 : etat.echecs()) + 1;
            Instant bloqueJusqua = echecs >= SEUIL_BLOCAGE ? clock.instant().plus(DUREE_BLOCAGE) : null;
            return new Etat(echecs, bloqueJusqua);
        });
    }

    public void reinitialiser(Long etudiantId) {
        etatsParEtudiant.remove(etudiantId);
    }
}
