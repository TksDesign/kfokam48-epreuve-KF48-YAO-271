package com.kfokam48.presencerelecture.repository;

import com.kfokam48.presencerelecture.domain.Relecture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RelectureRepository extends JpaRepository<Relecture, Long> {

    /** RG12 (etape 3) : jusqu'a 2 relectures possibles par exercice desormais - 409 se decide par relecteur, pas par exercice. */
    boolean existsByExerciceIdAndRelecteurId(Long exerciceId, Long relecteurId);

    /** Jusqu'a 2 relectures par exercice (RG12). */
    List<Relecture> findByExerciceId(Long exerciceId);

    /** Moyenne des notes recues par un etudiant sur les exercices dont il est l'auteur. */
    @Query("select avg(r.note) from Relecture r, Exercice e where e.id = r.exerciceId and e.auteurId = :auteurId")
    Double moyenneNotesRecues(Long auteurId);
}
