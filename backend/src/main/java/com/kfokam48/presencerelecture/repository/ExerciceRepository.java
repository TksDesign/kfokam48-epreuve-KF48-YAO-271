package com.kfokam48.presencerelecture.repository;

import com.kfokam48.presencerelecture.domain.Exercice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ExerciceRepository extends JpaRepository<Exercice, Long> {
    boolean existsBySessionIdAndAuteurId(Long sessionId, Long auteurId);

    List<Exercice> findByAuteurId(Long auteurId);

    long countByAuteurId(Long auteurId);

    /** RG : relectures "en attente" = exercices assignes a lui comme relecteur, pas encore evalues (CDC §7). */
    long countByRelecteurIdAndStatut(Long relecteurId, Exercice.Statut statut);

    List<Exercice> findByRelecteurIdAndStatut(Long relecteurId, Exercice.Statut statut);

    /**
     * RG12 (etape 3) : un exercice a maintenant jusqu'a 2 relecteurs (relecteurId
     * ou relecteur2Id). Un relecteur donne ne doit plus voir un exercice qu'il
     * a deja evalue lui-meme, meme si l'autre relecteur n'a pas encore rendu
     * le sien (l'exercice reste EN_ATTENTE dans ce cas).
     */
    @Query("""
            select e from Exercice e
            where e.statut = :statut
              and (e.relecteurId = :relecteurId or e.relecteur2Id = :relecteurId)
              and not exists (
                  select 1 from Relecture r where r.exerciceId = e.id and r.relecteurId = :relecteurId
              )
            """)
    List<Exercice> findAEvaluerPour(@Param("relecteurId") Long relecteurId, @Param("statut") Exercice.Statut statut);
}
