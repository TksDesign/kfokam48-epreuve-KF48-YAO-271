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

    /** EF3 : depot d'un exercice. EF4/RG4 : assignation d'un relecteur au hasard parmi les presents. */
    @Transactional
    public Exercice deposer(Long sessionId, Long etudiantId, String lien) {
        validerLien(lien);

        if (exerciceRepository.existsBySessionIdAndAuteurId(sessionId, etudiantId)) {
            throw new ExerciceDejaDeposeException();
        }

        Exercice exercice = new Exercice(lien, sessionId, etudiantId);

        List<Long> candidats = presenceRepository.trouverEtudiantsPresentsHorsAuteur(sessionId, etudiantId);
        if (!candidats.isEmpty()) {
            Long relecteur = candidats.get(random.nextInt(candidats.size()));
            exercice.assignerRelecteur(relecteur);
        }
        // Sinon : aucun candidat present, l'exercice reste DEPOSE (cf cahier des
        // charges section 7 - "assignation faite au moment du depot s'il y a des
        // candidats valides, sinon mise en file d'attente").

        return exerciceRepository.save(exercice);
    }

    /** EF5 : l'etudiant consulte ses propres exercices et, s'ils sont evalues, sa note. */
    @Transactional(readOnly = true)
    public List<MonExerciceResponse> mesExercices(Long etudiantId) {
        return exerciceRepository.findByAuteurId(etudiantId).stream()
                .map(exercice -> {
                    Relecture relecture = relectureRepository.findByExerciceId(exercice.getId()).orElse(null);
                    return new MonExerciceResponse(
                            exercice.getId(),
                            exercice.getLien(),
                            exercice.getStatut().name(),
                            relecture != null ? relecture.getNote() : null,
                            relecture != null ? relecture.getCommentaire() : null
                    );
                })
                .toList();
    }

    /** Exercices assignes a ce relecteur, pas encore evalues - pour l'ecran Relecteur (au lieu d'une liste figee en dur). */
    @Transactional(readOnly = true)
    public List<ExerciceAEvaluerResponse> aEvaluerPour(Long relecteurId) {
        return exerciceRepository.findByRelecteurIdAndStatut(relecteurId, Exercice.Statut.EN_ATTENTE).stream()
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
