package com.kfokam48.presencerelecture.service;

import com.kfokam48.presencerelecture.domain.Exercice;
import com.kfokam48.presencerelecture.domain.Relecture;
import com.kfokam48.presencerelecture.domain.exception.AutoRelectureException;
import com.kfokam48.presencerelecture.domain.exception.ExerciceInconnuException;
import com.kfokam48.presencerelecture.domain.exception.NoteInvalideException;
import com.kfokam48.presencerelecture.domain.exception.RelectureDejaRenduException;
import com.kfokam48.presencerelecture.repository.ExerciceRepository;
import com.kfokam48.presencerelecture.repository.RelectureRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;

@Service
public class RelectureService {

    private final ExerciceRepository exerciceRepository;
    private final RelectureRepository relectureRepository;
    private final Clock clock;

    public RelectureService(ExerciceRepository exerciceRepository, RelectureRepository relectureRepository, Clock clock) {
        this.exerciceRepository = exerciceRepository;
        this.relectureRepository = relectureRepository;
        this.clock = clock;
    }

    /**
     * EF5 : un des relecteurs assignes rend sa note et son commentaire.
     * RG12/RG13 (etape 3) : jusqu'a 2 relecteurs distincts par exercice.
     * Le 409 se decide desormais par relecteur (pas par exercice) - l'autre
     * relecteur peut toujours rendre la sienne independamment. L'exercice ne
     * passe EVALUE que quand tous les relecteurs assignes ont rendu.
     */
    @Transactional
    public void evaluer(Long exerciceId, Long relecteurId, Integer note, String commentaire) {
        if (note == null || note < 0 || note > 20) {
            throw new NoteInvalideException();
        }

        Exercice exercice = exerciceRepository.findById(exerciceId)
                .orElseThrow(ExerciceInconnuException::new);

        // RG3/EF4 : seul un relecteur reellement assigne (slot 1 ou 2) peut rendre cette relecture.
        boolean estAssigne = relecteurId.equals(exercice.getRelecteurId()) || relecteurId.equals(exercice.getRelecteur2Id());
        if (!estAssigne) {
            throw new AutoRelectureException();
        }

        if (relectureRepository.existsByExerciceIdAndRelecteurId(exerciceId, relecteurId)) {
            throw new RelectureDejaRenduException();
        }

        long relecturesDejaRendues = relectureRepository.findByExerciceId(exerciceId).size();
        relectureRepository.save(new Relecture(note, commentaire, clock.instant(), exerciceId, relecteurId));

        if (relecturesDejaRendues + 1 >= exercice.nombreRelecteursAssignes()) {
            exercice.marquerEvalue();
            exerciceRepository.save(exercice);
        }
    }
}
