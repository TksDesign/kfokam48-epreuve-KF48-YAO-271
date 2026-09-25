package com.kfokam48.presencerelecture.service;

import com.kfokam48.presencerelecture.domain.Exercice;
import com.kfokam48.presencerelecture.domain.exception.ExerciceDejaDeposeException;
import com.kfokam48.presencerelecture.domain.exception.LienInvalideException;
import com.kfokam48.presencerelecture.repository.ExerciceRepository;
import com.kfokam48.presencerelecture.repository.PresenceRepository;
import com.kfokam48.presencerelecture.repository.RelectureRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * EF3/EF4/RG4 : depot d'un exercice et assignation aleatoire du relecteur
 * parmi les etudiants presents, hors l'auteur (Q7).
 */
@ExtendWith(MockitoExtension.class)
class ExerciceServiceTest {

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
    void assigne_un_relecteur_quand_un_candidat_est_present() {
        when(exerciceRepository.existsBySessionIdAndAuteurId(1L, 10L)).thenReturn(false);
        when(presenceRepository.trouverEtudiantsPresentsHorsAuteur(1L, 10L)).thenReturn(List.of(20L));
        when(exerciceRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Exercice exercice = creerService().deposer(1L, 10L, "https://example.com/exo.pdf");

        assertThat(exercice.getStatut()).isEqualTo(Exercice.Statut.EN_ATTENTE);
        assertThat(exercice.getRelecteurId()).isEqualTo(20L);
    }

    @Test
    void assigne_deux_relecteurs_distincts_quand_deux_candidats_sont_presents() {
        when(exerciceRepository.existsBySessionIdAndAuteurId(1L, 10L)).thenReturn(false);
        when(presenceRepository.trouverEtudiantsPresentsHorsAuteur(1L, 10L)).thenReturn(List.of(20L, 30L));
        when(exerciceRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Exercice exercice = creerService().deposer(1L, 10L, "https://example.com/exo.pdf");

        assertThat(exercice.getStatut()).isEqualTo(Exercice.Statut.EN_ATTENTE);
        assertThat(exercice.getRelecteurId()).isNotNull();
        assertThat(exercice.getRelecteur2Id()).isNotNull();
        assertThat(exercice.getRelecteurId()).isNotEqualTo(exercice.getRelecteur2Id());
        assertThat(List.of(exercice.getRelecteurId(), exercice.getRelecteur2Id())).containsExactlyInAnyOrder(20L, 30L);
    }

    @Test
    void assigne_un_seul_relecteur_quand_un_seul_candidat_est_present() {
        when(exerciceRepository.existsBySessionIdAndAuteurId(1L, 10L)).thenReturn(false);
        when(presenceRepository.trouverEtudiantsPresentsHorsAuteur(1L, 10L)).thenReturn(List.of(20L));
        when(exerciceRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Exercice exercice = creerService().deposer(1L, 10L, "https://example.com/exo.pdf");

        assertThat(exercice.getRelecteurId()).isEqualTo(20L);
        assertThat(exercice.getRelecteur2Id()).isNull();
        assertThat(exercice.nombreRelecteursAssignes()).isEqualTo(1);
    }

    @Test
    void reste_depose_sans_candidat_present() {
        when(exerciceRepository.existsBySessionIdAndAuteurId(1L, 10L)).thenReturn(false);
        when(presenceRepository.trouverEtudiantsPresentsHorsAuteur(1L, 10L)).thenReturn(List.of());
        when(exerciceRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Exercice exercice = creerService().deposer(1L, 10L, "https://example.com/exo.pdf");

        assertThat(exercice.getStatut()).isEqualTo(Exercice.Statut.DEPOSE);
        assertThat(exercice.getRelecteurId()).isNull();
    }

    @Test
    void refuse_un_lien_qui_n_est_pas_une_url_absolue() {
        assertThatThrownBy(() -> creerService().deposer(1L, 10L, "pas-une-url"))
                .isInstanceOf(LienInvalideException.class);
    }

    @Test
    void refuse_un_deuxieme_depot_pour_le_meme_etudiant() {
        when(exerciceRepository.existsBySessionIdAndAuteurId(1L, 10L)).thenReturn(true);

        assertThatThrownBy(() -> creerService().deposer(1L, 10L, "https://example.com/exo.pdf"))
                .isInstanceOf(ExerciceDejaDeposeException.class);
    }
}
