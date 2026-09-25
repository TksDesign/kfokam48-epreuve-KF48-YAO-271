package com.kfokam48.presencerelecture.service;

import com.kfokam48.presencerelecture.domain.Etudiant;
import com.kfokam48.presencerelecture.domain.Exercice;
import com.kfokam48.presencerelecture.domain.exception.PromotionInconnueException;
import com.kfokam48.presencerelecture.repository.EtudiantRepository;
import com.kfokam48.presencerelecture.repository.ExerciceRepository;
import com.kfokam48.presencerelecture.repository.PresenceRepository;
import com.kfokam48.presencerelecture.repository.PromotionRepository;
import com.kfokam48.presencerelecture.repository.RelectureRepository;
import com.kfokam48.presencerelecture.web.dto.LigneTableauResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * EF6/Q16 : tableau recapitulatif - une ligne par etudiant de la promotion,
 * avec la moyenne nullable et les relectures "en attente" au bon sens
 * (celles qu'il doit encore FAIRE comme relecteur, cf CDC §7).
 */
@ExtendWith(MockitoExtension.class)
class TableauServiceTest {

    @Mock
    private PromotionRepository promotionRepository;
    @Mock
    private EtudiantRepository etudiantRepository;
    @Mock
    private PresenceRepository presenceRepository;
    @Mock
    private ExerciceRepository exerciceRepository;
    @Mock
    private RelectureRepository relectureRepository;

    private TableauService creerService() {
        return new TableauService(promotionRepository, etudiantRepository, presenceRepository,
                exerciceRepository, relectureRepository);
    }

    private Etudiant etudiant(Long id, String nom) {
        Etudiant e = mock(Etudiant.class);
        when(e.getId()).thenReturn(id);
        when(e.getNom()).thenReturn(nom);
        return e;
    }

    @Test
    void refuse_une_promotion_inconnue() {
        when(promotionRepository.existsById(999L)).thenReturn(false);

        assertThatThrownBy(() -> creerService().consulter(999L))
                .isInstanceOf(PromotionInconnueException.class);
    }

    @Test
    void construit_une_ligne_par_etudiant_avec_moyenne_nulle_sans_relecture() {
        Etudiant amina = etudiant(1L, "Amina");
        when(promotionRepository.existsById(1L)).thenReturn(true);
        when(etudiantRepository.findByPromotionId(1L)).thenReturn(List.of(amina));
        when(presenceRepository.countByEtudiantId(1L)).thenReturn(3L);
        when(exerciceRepository.countByAuteurId(1L)).thenReturn(2L);
        when(relectureRepository.moyenneNotesRecues(1L)).thenReturn(null);
        when(exerciceRepository.countByRelecteurIdAndStatut(1L, Exercice.Statut.EN_ATTENTE)).thenReturn(1L);

        List<LigneTableauResponse> tableau = creerService().consulter(1L);

        assertThat(tableau).hasSize(1);
        LigneTableauResponse ligne = tableau.get(0);
        assertThat(ligne.etudiantId()).isEqualTo(1L);
        assertThat(ligne.nom()).isEqualTo("Amina");
        assertThat(ligne.presences()).isEqualTo(3);
        assertThat(ligne.exercicesDeposes()).isEqualTo(2);
        assertThat(ligne.moyenne()).isNull();
        assertThat(ligne.relecturesEnAttente()).isEqualTo(1);
    }

    @Test
    void reporte_la_moyenne_quand_des_relectures_ont_ete_recues() {
        Etudiant brice = etudiant(2L, "Brice");
        when(promotionRepository.existsById(1L)).thenReturn(true);
        when(etudiantRepository.findByPromotionId(1L)).thenReturn(List.of(brice));
        when(presenceRepository.countByEtudiantId(2L)).thenReturn(1L);
        when(exerciceRepository.countByAuteurId(2L)).thenReturn(1L);
        when(relectureRepository.moyenneNotesRecues(2L)).thenReturn(16.0);
        when(exerciceRepository.countByRelecteurIdAndStatut(2L, Exercice.Statut.EN_ATTENTE)).thenReturn(0L);

        List<LigneTableauResponse> tableau = creerService().consulter(1L);

        assertThat(tableau.get(0).moyenne()).isEqualTo(16.0);
    }
}
