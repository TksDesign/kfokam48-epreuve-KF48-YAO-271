package com.kfokam48.presencerelecture.web;

import com.kfokam48.presencerelecture.domain.Presence;
import com.kfokam48.presencerelecture.domain.Session;
import com.kfokam48.presencerelecture.repository.ExerciceRepository;
import com.kfokam48.presencerelecture.repository.PresenceRepository;
import com.kfokam48.presencerelecture.repository.RelectureRepository;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test d'integration : Postgres ephemere via Testcontainers (B6). Utilise la
 * promotion 1 et les etudiants 1-5 charges par la migration V2 (seed demo).
 */
@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
class TableauControllerIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private PresenceRepository presenceRepository;

    @Autowired
    private ExerciceRepository exerciceRepository;

    @Autowired
    private RelectureRepository relectureRepository;

    @BeforeEach
    void construireUnScenarioComplet() throws Exception {
        // conteneur/BD partages entre les methodes de test de cette classe :
        // on repart d'un etat propre a chaque fois (ordre : enfants avant parents).
        relectureRepository.deleteAll();
        exerciceRepository.deleteAll();
        presenceRepository.deleteAll();
        sessionRepository.deleteAll();

        Instant maintenant = Instant.now();
        Session session = sessionRepository.save(new Session("Cours",
                "T" + System.nanoTime() % 100000, maintenant, maintenant.plus(15, ChronoUnit.MINUTES), 1L));
        Long sessionId = session.getId();

        // etudiant 1 (auteur) et 2 (relecteur) presents, exercice depose et evalue
        presenceRepository.save(new Presence(maintenant, Presence.Source.ETUDIANT, sessionId, 1L));
        presenceRepository.save(new Presence(maintenant, Presence.Source.ETUDIANT, sessionId, 2L));

        String reponse = mockMvc.perform(post("/api/exercices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"sessionId\":" + sessionId + ",\"etudiantId\":1,\"lien\":\"https://example.com/exo\"}"))
                .andReturn().getResponse().getContentAsString();
        Long exerciceId = Long.valueOf(reponse.replaceAll(".*\"id\":(\\d+).*", "$1"));

        mockMvc.perform(post("/api/relectures/{id}", exerciceId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"note\":16,\"commentaire\":\"bien\",\"relecteurId\":2}"));

        // deuxieme session ISOLEE : seuls 4 et 5 presents, assignation deterministe (5 est
        // le seul candidat) - sinon 1/2 seraient aussi candidats dans la meme session.
        Session session2 = sessionRepository.save(new Session("Cours 2",
                "T2" + System.nanoTime() % 100000, maintenant, maintenant.plus(15, ChronoUnit.MINUTES), 1L));
        Long session2Id = session2.getId();
        presenceRepository.save(new Presence(maintenant, Presence.Source.ETUDIANT, session2Id, 4L));
        presenceRepository.save(new Presence(maintenant, Presence.Source.ETUDIANT, session2Id, 5L));
        mockMvc.perform(post("/api/exercices")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"sessionId\":" + session2Id + ",\"etudiantId\":4,\"lien\":\"https://example.com/exo2\"}"));
    }

    @Test
    void l_auteur_evalue_a_sa_moyenne_et_pas_de_relecture_en_attente() throws Exception {
        mockMvc.perform(get("/api/tableau").param("promotionId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.etudiantId == 1)].presences").value(org.hamcrest.Matchers.contains(1)))
                .andExpect(jsonPath("$[?(@.etudiantId == 1)].exercicesDeposes").value(org.hamcrest.Matchers.contains(1)))
                .andExpect(jsonPath("$[?(@.etudiantId == 1)].moyenne").value(org.hamcrest.Matchers.contains(16.0)))
                .andExpect(jsonPath("$[?(@.etudiantId == 1)].relecturesEnAttente").value(org.hamcrest.Matchers.contains(0)));
    }

    @Test
    void le_relecteur_qui_n_a_pas_encore_evalue_a_une_relecture_en_attente() throws Exception {
        mockMvc.perform(get("/api/tableau").param("promotionId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.etudiantId == 5)].relecturesEnAttente").value(org.hamcrest.Matchers.contains(1)))
                .andExpect(jsonPath("$[?(@.etudiantId == 5)].moyenne").value(org.hamcrest.Matchers.contains(org.hamcrest.Matchers.nullValue())));
    }

    @Test
    void etudiant_sans_activite_a_des_zeros_et_moyenne_nulle() throws Exception {
        mockMvc.perform(get("/api/tableau").param("promotionId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.etudiantId == 3)].presences").value(org.hamcrest.Matchers.contains(0)))
                .andExpect(jsonPath("$[?(@.etudiantId == 3)].moyenne").value(org.hamcrest.Matchers.contains(org.hamcrest.Matchers.nullValue())));
    }

    @Test
    void refuse_une_promotion_inconnue_avec_404() throws Exception {
        mockMvc.perform(get("/api/tableau").param("promotionId", "999999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("PROMOTION_INCONNUE"));
    }
}
