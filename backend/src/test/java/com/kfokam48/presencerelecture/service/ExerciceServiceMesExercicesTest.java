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
        when(relectureRepository.findByExerciceId(exercice.getId())).thenReturn(List.of());

        List<MonExerciceResponse> resultat = creerService().mesExercices(10L);

        assertThat(resultat).hasSize(1);
        assertThat(resultat.get(0).statut()).isEqualTo("DEPOSE");
        assertThat(resultat.get(0).note()).isNull();
        assertThat(resultat.get(0).commentaire()).isNull();
        assertThat(resultat.get(0).provisoire()).isFalse();
    }

    @Test
    void note_provisoire_quand_un_seul_des_deux_relecteurs_a_rendu() {
        Exercice exercice = new Exercice("https://example.com/exo", 1L, 10L);
        exercice.assignerRelecteurs(20L, 30L);
        Relecture relecture = new Relecture(18, "en cours de relecture", Instant.now(), exercice.getId(), 20L);

        when(exerciceRepository.findByAuteurId(10L)).thenReturn(List.of(exercice));
        when(relectureRepository.findByExerciceId(exercice.getId())).thenReturn(List.of(relecture));

        List<MonExerciceResponse> resultat = creerService().mesExercices(10L);

        assertThat(resultat.get(0).statut()).isEqualTo("EN_ATTENTE");
        assertThat(resultat.get(0).note()).isEqualTo(18.0);
        assertThat(resultat.get(0).provisoire()).isTrue();
    }

    @Test
    void retourne_la_moyenne_une_fois_les_deux_relectures_rendues() {
        Exercice exercice = new Exercice("https://example.com/exo", 1L, 10L);
        exercice.assignerRelecteurs(20L, 30L);
        exercice.marquerEvalue();
        Instant t1 = Instant.parse("2026-09-25T10:00:00Z");
        Instant t2 = Instant.parse("2026-09-25T11:00:00Z");
        Relecture relecture1 = new Relecture(18, "premiere relecture", t1, exercice.getId(), 20L);
        Relecture relecture2 = new Relecture(12, "deuxieme relecture, plus recente", t2, exercice.getId(), 30L);

        when(exerciceRepository.findByAuteurId(10L)).thenReturn(List.of(exercice));
        when(relectureRepository.findByExerciceId(exercice.getId())).thenReturn(List.of(relecture1, relecture2));

        List<MonExerciceResponse> resultat = creerService().mesExercices(10L);

        assertThat(resultat.get(0).statut()).isEqualTo("EVALUE");
        assertThat(resultat.get(0).note()).isEqualTo(15.0);
        assertThat(resultat.get(0).commentaire()).isEqualTo("deuxieme relecture, plus recente");
        assertThat(resultat.get(0).provisoire()).isFalse();
    }
}
