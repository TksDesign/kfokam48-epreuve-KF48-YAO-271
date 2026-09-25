package com.kfokam48.presencerelecture.service;

import com.kfokam48.presencerelecture.domain.Etudiant;
import com.kfokam48.presencerelecture.domain.Exercice;
import com.kfokam48.presencerelecture.domain.exception.PromotionInconnueException;
import com.kfokam48.presencerelecture.repository.EtudiantRepository;
import com.kfokam48.presencerelecture.repository.ExerciceRepository;
import com.kfokam48.presencerelecture.repository.PresenceRepository;
import com.kfokam48.presencerelecture.repository.PromotionRepository;
import com.kfokam48.presencerelecture.repository.RelectureRepository;
import com.kfokam48.presencerelecture.web.dto.LigneTableauResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TableauService {

    private final PromotionRepository promotionRepository;
    private final EtudiantRepository etudiantRepository;
    private final PresenceRepository presenceRepository;
    private final ExerciceRepository exerciceRepository;
    private final RelectureRepository relectureRepository;

    public TableauService(PromotionRepository promotionRepository, EtudiantRepository etudiantRepository,
                           PresenceRepository presenceRepository, ExerciceRepository exerciceRepository,
                           RelectureRepository relectureRepository) {
        this.promotionRepository = promotionRepository;
        this.etudiantRepository = etudiantRepository;
        this.presenceRepository = presenceRepository;
        this.exerciceRepository = exerciceRepository;
        this.relectureRepository = relectureRepository;
    }

    /** EF6/Q16 : tableau recapitulatif du formateur, par etudiant. */
    @Transactional(readOnly = true)
    public List<LigneTableauResponse> consulter(Long promotionId) {
        if (!promotionRepository.existsById(promotionId)) {
            throw new PromotionInconnueException();
        }

        return etudiantRepository.findByPromotionId(promotionId).stream()
                .map(this::construireLigne)
                .toList();
    }

    private LigneTableauResponse construireLigne(Etudiant etudiant) {
        long presences = presenceRepository.countByEtudiantId(etudiant.getId());
        long exercicesDeposes = exerciceRepository.countByAuteurId(etudiant.getId());
        Double moyenne = relectureRepository.moyenneNotesRecues(etudiant.getId());
        long relecturesEnAttente = exerciceRepository.countByRelecteurIdAndStatut(etudiant.getId(), Exercice.Statut.EN_ATTENTE);

        return new LigneTableauResponse(etudiant.getId(), etudiant.getNom(), presences, exercicesDeposes,
                moyenne, relecturesEnAttente);
    }
}
