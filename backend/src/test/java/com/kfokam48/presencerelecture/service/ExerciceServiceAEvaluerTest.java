package com.kfokam48.presencerelecture.service;

import com.kfokam48.presencerelecture.domain.Exercice;
import com.kfokam48.presencerelecture.repository.ExerciceRepository;
import com.kfokam48.presencerelecture.repository.PresenceRepository;
import com.kfokam48.presencerelecture.repository.RelectureRepository;
import com.kfokam48.presencerelecture.web.dto.ExerciceAEvaluerResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * Ecran Relecteur : liste reelle des exercices assignes, pas encore evalues
 * (remplace la liste figee en dur cote front).
 */
@ExtendWith(MockitoExtension.class)
class ExerciceServiceAEvaluerTest {

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
    void retourne_les_exercices_en_attente_assignes_au_relecteur() {
        Exercice exercice = new Exercice("https://example.com/exo", 1L, 10L);
        exercice.assignerRelecteur(20L);
        when(exerciceRepository.findAEvaluerPour(20L, Exercice.Statut.EN_ATTENTE))
                .thenReturn(List.of(exercice));

        List<ExerciceAEvaluerResponse> resultat = creerService().aEvaluerPour(20L);

        assertThat(resultat).hasSize(1);
        assertThat(resultat.get(0).id()).isEqualTo(exercice.getId());
        assertThat(resultat.get(0).lien()).isEqualTo("https://example.com/exo");
    }

    @Test
    void retourne_les_exercices_assignes_au_deuxieme_relecteur_aussi() {
        Exercice exercice = new Exercice("https://example.com/exo", 1L, 10L);
        exercice.assignerRelecteurs(20L, 30L);
        when(exerciceRepository.findAEvaluerPour(30L, Exercice.Statut.EN_ATTENTE))
                .thenReturn(List.of(exercice));

        List<ExerciceAEvaluerResponse> resultat = creerService().aEvaluerPour(30L);

        assertThat(resultat).hasSize(1);
        assertThat(resultat.get(0).id()).isEqualTo(exercice.getId());
    }

    @Test
    void liste_vide_si_rien_a_evaluer() {
        when(exerciceRepository.findAEvaluerPour(99L, Exercice.Statut.EN_ATTENTE))
                .thenReturn(List.of());

        assertThat(creerService().aEvaluerPour(99L)).isEmpty();
    }
}
