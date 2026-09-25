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

    /** EF5 : le relecteur assigne rend sa note et son commentaire. */
    @Transactional
    public void evaluer(Long exerciceId, Long relecteurId, Integer note, String commentaire) {
        if (note == null || note < 0 || note > 20) {
            throw new NoteInvalideException();
        }

        Exercice exercice = exerciceRepository.findById(exerciceId)
                .orElseThrow(ExerciceInconnuException::new);

        // RG3/EF4 : seul le relecteur reellement assigne peut rendre cette relecture.
        if (exercice.getRelecteurId() == null || !exercice.getRelecteurId().equals(relecteurId)) {
            throw new AutoRelectureException();
        }

        if (relectureRepository.existsByExerciceId(exerciceId)) {
            throw new RelectureDejaRenduException();
        }

        relectureRepository.save(new Relecture(note, commentaire, clock.instant(), exerciceId));
        exercice.marquerEvalue();
        exerciceRepository.save(exercice);
    }
}
