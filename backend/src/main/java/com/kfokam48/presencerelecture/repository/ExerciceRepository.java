package com.kfokam48.presencerelecture.repository;

import com.kfokam48.presencerelecture.domain.Exercice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExerciceRepository extends JpaRepository<Exercice, Long> {
    boolean existsBySessionIdAndAuteurId(Long sessionId, Long auteurId);

    List<Exercice> findByAuteurId(Long auteurId);

    long countByAuteurId(Long auteurId);

    /** RG : relectures "en attente" = exercices assignes a lui comme relecteur, pas encore evalues (CDC §7). */
    long countByRelecteurIdAndStatut(Long relecteurId, Exercice.Statut statut);

    List<Exercice> findByRelecteurIdAndStatut(Long relecteurId, Exercice.Statut statut);
}
