package com.kfokam48.presencerelecture.service;

import com.kfokam48.presencerelecture.domain.Exercice;
import com.kfokam48.presencerelecture.domain.Relecture;
import com.kfokam48.presencerelecture.repository.ExerciceRepository;
import com.kfokam48.presencerelecture.repository.PresenceRepository;
import com.kfokam48.presencerelecture.repository.RelectureRepository;
import com.kfokam48.presencerelecture.web.dto.MonExerciceResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * EF5 : l'etudiant consulte ses propres exercices, avec la note recue si
 * evalue - et jamais l'identite du relecteur (RG10, verifie par le type
 * de retour MonExerciceResponse qui n'a pas de champ relecteurId).
 */
@ExtendWith(MockitoExtension.class)
class ExerciceServiceMesExercicesTest {

    @Mock
    private ExerciceRepository exerciceRepository;
    @Mock
    private PresenceRepository presenceRepository;
    @Mock
    private RelectureRepository relectureRepository;

    private ExerciceService creerService() {
        return new ExerciceService(exerciceRepository, presenceRepository, relectureRepository, new Random());
    }

    @Test
    void note_et_commentaire_nulls_tant_que_non_evalue() {
        Exercice exercice = new Exercice("https://example.com/exo", 1L, 10L);
        when(exerciceRepository.findByAuteurId(10L)).thenReturn(List.of(exercice));
        when(relectureRepository.findByExerciceId(exercice.getId())).thenReturn(Optional.empty());

        List<MonExerciceResponse> resultat = creerService().mesExercices(10L);

        assertThat(resultat).hasSize(1);
        assertThat(resultat.get(0).statut()).isEqualTo("DEPOSE");
        assertThat(resultat.get(0).note()).isNull();
        assertThat(resultat.get(0).commentaire()).isNull();
    }

    @Test
    void retourne_la_note_et_le_commentaire_une_fois_evalue() {
        Exercice exercice = new Exercice("https://example.com/exo", 1L, 10L);
        exercice.assignerRelecteur(20L);
        exercice.marquerEvalue();
        Relecture relecture = new Relecture(18, "excellent travail", Instant.now(), exercice.getId());

        when(exerciceRepository.findByAuteurId(10L)).thenReturn(List.of(exercice));
        when(relectureRepository.findByExerciceId(exercice.getId())).thenReturn(Optional.of(relecture));

        List<MonExerciceResponse> resultat = creerService().mesExercices(10L);

        assertThat(resultat.get(0).statut()).isEqualTo("EVALUE");
        assertThat(resultat.get(0).note()).isEqualTo(18);
        assertThat(resultat.get(0).commentaire()).isEqualTo("excellent travail");
    }
}
