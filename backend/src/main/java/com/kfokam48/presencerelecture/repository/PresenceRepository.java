package com.kfokam48.presencerelecture.repository;

import com.kfokam48.presencerelecture.domain.Presence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PresenceRepository extends JpaRepository<Presence, Long> {
    boolean existsBySessionIdAndEtudiantId(Long sessionId, Long etudiantId);

    /** RG4 : candidats relecteurs = etudiants presents a la session, hors l'auteur. */
    @Query("select distinct p.etudiantId from Presence p where p.sessionId = :sessionId and p.etudiantId <> :auteurId")
    List<Long> trouverEtudiantsPresentsHorsAuteur(Long sessionId, Long auteurId);
}
