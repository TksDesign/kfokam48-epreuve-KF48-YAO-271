package com.kfokam48.presencerelecture.service;

import com.kfokam48.presencerelecture.domain.Exercice;
import com.kfokam48.presencerelecture.domain.Relecture;
import com.kfokam48.presencerelecture.domain.exception.ExerciceDejaDeposeException;
import com.kfokam48.presencerelecture.domain.exception.LienInvalideException;
import com.kfokam48.presencerelecture.repository.ExerciceRepository;
import com.kfokam48.presencerelecture.repository.PresenceRepository;
import com.kfokam48.presencerelecture.repository.RelectureRepository;
import com.kfokam48.presencerelecture.web.dto.ExerciceAEvaluerResponse;
import com.kfokam48.presencerelecture.web.dto.MonExerciceResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

@Service
public class ExerciceService {

    private final ExerciceRepository exerciceRepository;
    private final PresenceRepository presenceRepository;
    private final RelectureRepository relectureRepository;
    private final Random random;

    public ExerciceService(ExerciceRepository exerciceRepository, PresenceRepository presenceRepository,
                            RelectureRepository relectureRepository, Random random) {
        this.exerciceRepository = exerciceRepository;
        this.presenceRepository = presenceRepository;
        this.relectureRepository = relectureRepository;
        this.random = random;
    }

    /** EF3 : depot d'un exercice. RG12 (etape 3) : assignation de 2 relecteurs distincts au hasard parmi les presents. */
    @Transactional
    public Exercice deposer(Long sessionId, Long etudiantId, String lien) {
        validerLien(lien);

        if (exerciceRepository.existsBySessionIdAndAuteurId(sessionId, etudiantId)) {
            throw new ExerciceDejaDeposeException();
        }

        Exercice exercice = new Exercice(lien, sessionId, etudiantId);

        List<Long> candidats = new ArrayList<>(presenceRepository.trouverEtudiantsPresentsHorsAuteur(sessionId, etudiantId));
        if (!candidats.isEmpty()) {
            Collections.shuffle(candidats, random);
            Long relecteur1 = candidats.get(0);
            Long relecteur2 = candidats.size() > 1 ? candidats.get(1) : null;
            exercice.assignerRelecteurs(relecteur1, relecteur2);
        }
        // Sinon (0 candidat) ou un seul relecteur assigne (1 candidat) : cf CDC
        // §7bis - la reassignation differee reste hors perimetre v1.0.

        return exerciceRepository.save(exercice);
    }

    /**
     * EF5 : l'etudiant consulte ses propres exercices et, s'ils sont evalues, sa note.
     * RG12/RG13 (etape 3) : moyenne des 2 relectures si les 2 sont rendues, sinon
     * note du seul relecteur ayant rendu, marquee provisoire.
     */
    @Transactional(readOnly = true)
    public List<MonExerciceResponse> mesExercices(Long etudiantId) {
        return exerciceRepository.findByAuteurId(etudiantId).stream()
                .map(exercice -> {
                    List<Relecture> relectures = relectureRepository.findByExerciceId(exercice.getId());
                    Double note = null;
                    String commentaire = null;
                    boolean provisoire = false;

                    if (relectures.size() >= 2) {
                        note = relectures.stream().mapToInt(Relecture::getNote).average().orElseThrow();
                        commentaire = relectures.stream().max(Comparator.comparing(Relecture::getDate))
                                .map(Relecture::getCommentaire).orElse(null);
                    } else if (relectures.size() == 1) {
                        note = relectures.get(0).getNote().doubleValue();
                        commentaire = relectures.get(0).getCommentaire();
                        provisoire = true;
                    }

                    return new MonExerciceResponse(
                            exercice.getId(),
                            exercice.getLien(),
                            exercice.getStatut().name(),
                            note,
                            commentaire,
                            provisoire
                    );
                })
                .toList();
    }

    /**
     * Exercices assignes a ce relecteur, pas encore evalues par LUI - pour
     * l'ecran Relecteur. RG12 (etape 3) : couvre les 2 slots de relecteur, et
     * exclut ceux ou ce relecteur a deja rendu sa propre relecture (l'autre
     * relecteur peut encore etre en attente sans que ca le concerne).
     */
    @Transactional(readOnly = true)
    public List<ExerciceAEvaluerResponse> aEvaluerPour(Long relecteurId) {
        return exerciceRepository.findAEvaluerPour(relecteurId, Exercice.Statut.EN_ATTENTE).stream()
                .map(ExerciceAEvaluerResponse::depuis)
                .toList();
    }

    private void validerLien(String lien) {
        try {
            URI uri = new URI(lien);
            if (!uri.isAbsolute()) {
                throw new LienInvalideException();
            }
        } catch (URISyntaxException e) {
            throw new LienInvalideException();
        }
    }
}
