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
 * EF3, EF4/RG4 (assignation), RG11 et le format du contrat.
 */
@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
class ExerciceControllerIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private PresenceRepository presenceRepository;

    private Long sessionAvecCandidat;
    private Long sessionSansCandidat;

    @BeforeEach
    void creerSessionsEtPresences() {
        Instant maintenant = Instant.now();

        Session avecCandidat = sessionRepository.save(new Session("Avec candidat",
                "C1" + System.nanoTime() % 100000, maintenant, maintenant.plus(15, ChronoUnit.MINUTES), 1L));
        sessionAvecCandidat = avecCandidat.getId();
        presenceRepository.save(new Presence(maintenant, Presence.Source.ETUDIANT, sessionAvecCandidat, 1L)); // auteur
        presenceRepository.save(new Presence(maintenant, Presence.Source.ETUDIANT, sessionAvecCandidat, 2L)); // relecteur potentiel

        Session sansCandidat = sessionRepository.save(new Session("Sans candidat",
                "C2" + System.nanoTime() % 100000, maintenant, maintenant.plus(15, ChronoUnit.MINUTES), 1L));
        sessionSansCandidat = sansCandidat.getId();
        presenceRepository.save(new Presence(maintenant, Presence.Source.ETUDIANT, sessionSansCandidat, 3L)); // seul present = l'auteur
    }

    @Test
    void depose_un_exercice_et_assigne_un_relecteur_parmi_les_presents() throws Exception {
        mockMvc.perform(post("/api/exercices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"sessionId\":" + sessionAvecCandidat + ",\"etudiantId\":1,\"lien\":\"https://example.com/exo\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.statut").value("EN_ATTENTE"));
    }

    @Test
    void reste_depose_si_aucun_autre_etudiant_present() throws Exception {
        mockMvc.perform(post("/api/exercices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"sessionId\":" + sessionSansCandidat + ",\"etudiantId\":3,\"lien\":\"https://example.com/exo\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.statut").value("DEPOSE"));
    }

    @Test
    void refuse_un_lien_invalide_avec_400() throws Exception {
        mockMvc.perform(post("/api/exercices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"sessionId\":" + sessionAvecCandidat + ",\"etudiantId\":1,\"lien\":\"pas-une-url\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("LIEN_INVALIDE"));
    }

    @Test
    void refuse_un_deuxieme_depot_avec_409() throws Exception {
        mockMvc.perform(post("/api/exercices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"sessionId\":" + sessionAvecCandidat + ",\"etudiantId\":1,\"lien\":\"https://example.com/exo\"}"))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/exercices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"sessionId\":" + sessionAvecCandidat + ",\"etudiantId\":1,\"lien\":\"https://example.com/autre\"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("EXERCICE_DEJA_DEPOSE"));
    }
}
