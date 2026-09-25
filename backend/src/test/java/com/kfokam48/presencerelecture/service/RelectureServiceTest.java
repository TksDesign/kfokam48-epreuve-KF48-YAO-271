package com.kfokam48.presencerelecture.service;

import com.kfokam48.presencerelecture.domain.Exercice;
import com.kfokam48.presencerelecture.domain.Relecture;
import com.kfokam48.presencerelecture.domain.exception.AutoRelectureException;
import com.kfokam48.presencerelecture.domain.exception.ExerciceInconnuException;
import com.kfokam48.presencerelecture.domain.exception.NoteInvalideException;
import com.kfokam48.presencerelecture.domain.exception.RelectureDejaRenduException;
import com.kfokam48.presencerelecture.repository.ExerciceRepository;
import com.kfokam48.presencerelecture.repository.RelectureRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * EF5, RG3 (auto-relecture / seul le relecteur assigne), RG5 (note 0-20),
 * RG7 (relecture definitive).
 */
@ExtendWith(MockitoExtension.class)
class RelectureServiceTest {

    @Mock
    private ExerciceRepository exerciceRepository;

    @Mock
    private RelectureRepository relectureRepository;

    private final Clock clock = Clock.fixed(Instant.parse("2026-09-25T10:00:00Z"), ZoneOffset.UTC);

    private RelectureService creerService() {
        return new RelectureService(exerciceRepository, relectureRepository, clock);
    }

    private Exercice exerciceAvecRelecteur(Long relecteurId) {
        Exercice exercice = new Exercice("https://example.com/exo", 1L, 10L);
        exercice.assignerRelecteur(relecteurId);
        return exercice;
    }

    @Test
    void refuse_une_note_hors_bornes() {
        assertThatThrownBy(() -> creerService().evaluer(1L, 20L, 25, "trop haute"))
                .isInstanceOf(NoteInvalideException.class);
        assertThatThrownBy(() -> creerService().evaluer(1L, 20L, -1, "negative"))
                .isInstanceOf(NoteInvalideException.class);
    }

    @Test
    void refuse_un_exercice_inconnu() {
        when(exerciceRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> creerService().evaluer(999L, 20L, 15, "ok"))
                .isInstanceOf(ExerciceInconnuException.class);
    }

    @Test
    void refuse_si_le_relecteur_n_est_pas_celui_assigne() {
        when(exerciceRepository.findById(1L)).thenReturn(Optional.of(exerciceAvecRelecteur(20L)));

        assertThatThrownBy(() -> creerService().evaluer(1L, 99L, 15, "je ne suis pas assigne"))
                .isInstanceOf(AutoRelectureException.class);
    }

    @Test
    void refuse_une_relecture_deja_rendue_par_ce_relecteur() {
        when(exerciceRepository.findById(1L)).thenReturn(Optional.of(exerciceAvecRelecteur(20L)));
        when(relectureRepository.existsByExerciceIdAndRelecteurId(1L, 20L)).thenReturn(true);

        assertThatThrownBy(() -> creerService().evaluer(1L, 20L, 15, "deja fait"))
                .isInstanceOf(RelectureDejaRenduException.class);
    }

    @Test
    void enregistre_la_relecture_et_passe_l_exercice_a_evalue_quand_un_seul_relecteur_assigne() {
        Exercice exercice = exerciceAvecRelecteur(20L);
        when(exerciceRepository.findById(1L)).thenReturn(Optional.of(exercice));
        when(relectureRepository.existsByExerciceIdAndRelecteurId(1L, 20L)).thenReturn(false);
        when(relectureRepository.findByExerciceId(1L)).thenReturn(List.of());
        when(exerciceRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        assertThatCode(() -> creerService().evaluer(1L, 20L, 15, "bon travail")).doesNotThrowAnyException();
        assertThat(exercice.getStatut()).isEqualTo(Exercice.Statut.EVALUE);
    }

    @Test
    void reste_en_attente_quand_un_seul_des_deux_relecteurs_a_rendu() {
        Exercice exercice = new Exercice("https://example.com/exo", 1L, 10L);
        exercice.assignerRelecteurs(20L, 30L);
        when(exerciceRepository.findById(1L)).thenReturn(Optional.of(exercice));
        when(relectureRepository.existsByExerciceIdAndRelecteurId(1L, 20L)).thenReturn(false);
        when(relectureRepository.findByExerciceId(1L)).thenReturn(List.of());

        creerService().evaluer(1L, 20L, 15, "premiere des deux");

        assertThat(exercice.getStatut()).isEqualTo(Exercice.Statut.EN_ATTENTE);
    }

    @Test
    void passe_evalue_quand_le_deuxieme_relecteur_rend_aussi() {
        Exercice exercice = new Exercice("https://example.com/exo", 1L, 10L);
        exercice.assignerRelecteurs(20L, 30L);
        Relecture dejaRendue = new Relecture(18, "premiere", Instant.now(), exercice.getId(), 20L);
        when(exerciceRepository.findById(1L)).thenReturn(Optional.of(exercice));
        when(relectureRepository.existsByExerciceIdAndRelecteurId(1L, 30L)).thenReturn(false);
        when(relectureRepository.findByExerciceId(1L)).thenReturn(List.of(dejaRendue));
        when(exerciceRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        creerService().evaluer(1L, 30L, 12, "deuxieme");

        assertThat(exercice.getStatut()).isEqualTo(Exercice.Statut.EVALUE);
    }

    @Test
    void le_deuxieme_relecteur_peut_rendre_meme_si_le_premier_n_a_pas_encore_rendu() {
        Exercice exercice = new Exercice("https://example.com/exo", 1L, 10L);
        exercice.assignerRelecteurs(20L, 30L);
        when(exerciceRepository.findById(1L)).thenReturn(Optional.of(exercice));
        when(relectureRepository.existsByExerciceIdAndRelecteurId(1L, 30L)).thenReturn(false);
        when(relectureRepository.findByExerciceId(1L)).thenReturn(List.of());

        assertThatCode(() -> creerService().evaluer(1L, 30L, 12, "je passe avant l'autre")).doesNotThrowAnyException();
    }
}
