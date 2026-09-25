package com.kfokam48.presencerelecture.service;

import com.kfokam48.presencerelecture.domain.Exercice;
import com.kfokam48.presencerelecture.domain.exception.ExerciceDejaDeposeException;
import com.kfokam48.presencerelecture.domain.exception.LienInvalideException;
import com.kfokam48.presencerelecture.repository.ExerciceRepository;
import com.kfokam48.presencerelecture.repository.PresenceRepository;
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
    private final Random random;

    public ExerciceService(ExerciceRepository exerciceRepository, PresenceRepository presenceRepository, Random random) {
        this.exerciceRepository = exerciceRepository;
        this.presenceRepository = presenceRepository;
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
