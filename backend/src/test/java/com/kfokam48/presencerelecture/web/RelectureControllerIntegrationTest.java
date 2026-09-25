package com.kfokam48.presencerelecture.web;

import com.kfokam48.presencerelecture.domain.Presence;
import com.kfokam48.presencerelecture.domain.Session;
import com.kfokam48.presencerelecture.repository.PresenceRepository;
import com.kfokam48.presencerelecture.repository.SessionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test d'integration : Postgres ephemere via Testcontainers (B6). Couvre
 * EF5, RG3 (relecteur assigne uniquement), RG5, RG7 et le format du contrat.
 */
@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
class RelectureControllerIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private PresenceRepository presenceRepository;

    private Long sessionId;

    @BeforeEach
    void creerSessionEtPresences() {
        Instant maintenant = Instant.now();
        Session session = sessionRepository.save(new Session("Cours",
                "R" + System.nanoTime() % 100000, maintenant, maintenant.plus(15, ChronoUnit.MINUTES), 1L));
        sessionId = session.getId();
        // auteur = 1, seul candidat relecteur present = 2 -> assignation deterministe
        presenceRepository.save(new Presence(maintenant, Presence.Source.ETUDIANT, sessionId, 1L));
        presenceRepository.save(new Presence(maintenant, Presence.Source.ETUDIANT, sessionId, 2L));
    }

    private Long deposerExerciceEtRecupererId(Long auteurId, String lien) throws Exception {
        String reponse = mockMvc.perform(post("/api/exercices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"sessionId\":" + sessionId + ",\"etudiantId\":" + auteurId + ",\"lien\":\"" + lien + "\"}"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return Long.valueOf(reponse.replaceAll(".*\"id\":(\\d+).*", "$1"));
    }

    @Test
    void enregistre_la_relecture_du_relecteur_assigne() throws Exception {
        Long exerciceId = deposerExerciceEtRecupererId(1L, "https://example.com/exo1");

        mockMvc.perform(post("/api/relectures/{id}", exerciceId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"note\":18,\"commentaire\":\"tres bien\",\"relecteurId\":2}"))
                .andExpect(status().isOk());
    }

    @Test
    void refuse_si_ce_n_est_pas_le_relecteur_assigne() throws Exception {
        Long exerciceId = deposerExerciceEtRecupererId(1L, "https://example.com/exo2");

        mockMvc.perform(post("/api/relectures/{id}", exerciceId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"note\":18,\"commentaire\":\"pas moi\",\"relecteurId\":1}"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("AUTO_RELECTURE"));
    }

    @Test
    void refuse_une_note_hors_bornes_avec_400() throws Exception {
        Long exerciceId = deposerExerciceEtRecupererId(1L, "https://example.com/exo3");

        mockMvc.perform(post("/api/relectures/{id}", exerciceId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"note\":25,\"commentaire\":\"trop haut\",\"relecteurId\":2}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("NOTE_INVALIDE"));
    }

    @Test
    void refuse_une_deuxieme_relecture_avec_409() throws Exception {
        Long exerciceId = deposerExerciceEtRecupererId(1L, "https://example.com/exo4");

        mockMvc.perform(post("/api/relectures/{id}", exerciceId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"note\":15,\"commentaire\":\"ok\",\"relecteurId\":2}"))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/relectures/{id}", exerciceId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"note\":10,\"commentaire\":\"deuxieme essai\",\"relecteurId\":2}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("RELECTURE_DEJA_RENDUE"));
    }

    @Test
    void refuse_un_exercice_inconnu_avec_404() throws Exception {
        mockMvc.perform(post("/api/relectures/{id}", 999_999)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"note\":15,\"commentaire\":\"ok\",\"relecteurId\":2}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("EXERCICE_INCONNU"));
    }
}
