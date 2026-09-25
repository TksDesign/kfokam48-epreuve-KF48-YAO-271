package com.kfokam48.presencerelecture.web;

import com.kfokam48.presencerelecture.domain.Session;
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
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test d'integration : Postgres ephemere via Testcontainers (B6). Couvre
 * EF1, RG1 (expiration) et RG2 (blocage apres 5 codes inconnus).
 */
@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
class PresenceControllerIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SessionRepository sessionRepository;

    private String codeValide;
    private String codeExpire;

    @BeforeEach
    void creerSessions() {
        Instant maintenant = Instant.now();
        Session sessionValide = new Session("Cours valide", "OK" + System.nanoTime() % 100000,
                maintenant, maintenant.plus(15, ChronoUnit.MINUTES), 1L);
        codeValide = sessionRepository.save(sessionValide).getCode();

        Session sessionExpiree = new Session("Cours expire", "EXP" + System.nanoTime() % 100000,
                maintenant.minus(20, ChronoUnit.MINUTES), maintenant.minus(5, ChronoUnit.MINUTES), 1L);
        codeExpire = sessionRepository.save(sessionExpiree).getCode();
    }

    @Test
    void marque_la_presence_avec_un_code_valide() throws Exception {
        mockMvc.perform(post("/api/presences")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"code\":\"" + codeValide + "\",\"etudiantId\":1}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.source").value("ETUDIANT"))
                .andExpect(jsonPath("$.etudiantId").value(1));
    }

    @Test
    void refuse_un_code_inconnu_avec_400() throws Exception {
        mockMvc.perform(post("/api/presences")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"code\":\"INEXISTANT\",\"etudiantId\":2}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("CODE_INCONNU"));
    }

    @Test
    void refuse_un_code_expire_avec_410() throws Exception {
        mockMvc.perform(post("/api/presences")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"code\":\"" + codeExpire + "\",\"etudiantId\":3}"))
                .andExpect(status().isGone())
                .andExpect(jsonPath("$.code").value("CODE_EXPIRE"));
    }

    @Test
    void refuse_une_deuxieme_presence_pour_le_meme_etudiant_avec_409() throws Exception {
        mockMvc.perform(post("/api/presences")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"code\":\"" + codeValide + "\",\"etudiantId\":4}"))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/presences")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"code\":\"" + codeValide + "\",\"etudiantId\":4}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("DEJA_PRESENT"));
    }

    @Test
    void bloque_apres_5_codes_inconnus_avec_429() throws Exception {
        for (int i = 0; i < 5; i++) {
            mockMvc.perform(post("/api/presences")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"code\":\"MAUVAIS\",\"etudiantId\":5}"))
                    .andExpect(status().isBadRequest());
        }

        mockMvc.perform(post("/api/presences")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"code\":\"" + codeValide + "\",\"etudiantId\":5}"))
                .andExpect(status().isTooManyRequests())
                .andExpect(jsonPath("$.code").value("TROP_DE_TENTATIVES"));
    }

    /** Issue #4, EF7/RG8 : le formateur ajoute une presence sans code. */
    @Test
    void ajoute_une_presence_manuelle_source_formateur() throws Exception {
        mockMvc.perform(post("/api/sessions/{id}/presences-manuelles", sessionRepository.findByCode(codeValide).get().getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"etudiantId\":1}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.source").value("FORMATEUR"))
                .andExpect(jsonPath("$.etudiantId").value(1));
    }

    @Test
    void refuse_une_presence_manuelle_si_deja_present_avec_409() throws Exception {
        Long sessionId = sessionRepository.findByCode(codeValide).get().getId();

        mockMvc.perform(post("/api/sessions/{id}/presences-manuelles", sessionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"etudiantId\":3}"))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/sessions/{id}/presences-manuelles", sessionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"etudiantId\":3}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("DEJA_PRESENT"));
    }

    @Test
    void refuse_une_presence_manuelle_pour_une_session_inconnue_avec_404() throws Exception {
        mockMvc.perform(post("/api/sessions/{id}/presences-manuelles", 999_999)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"etudiantId\":1}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("SESSION_INCONNUE"));
    }

    @Test
    void refuse_une_presence_manuelle_pour_un_etudiant_inconnu_avec_404() throws Exception {
        Long sessionId = sessionRepository.findByCode(codeValide).get().getId();

        mockMvc.perform(post("/api/sessions/{id}/presences-manuelles", sessionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"etudiantId\":999999}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("ETUDIANT_INCONNU"));
    }

    /**
     * Issue #37 : deux requetes quasi simultanees pour le meme etudiant/session
     * (double clic, double onglet) ne doivent jamais produire un 500 -
     * la seconde doit recevoir un 409 DEJA_PRESENT propre, pas une exception
     * de contrainte DB non traduite.
     */
    @Test
    void deux_requetes_concurrentes_meme_etudiant_ne_donnent_jamais_500() throws Exception {
        int nbThreads = 2;
        ExecutorService executor = Executors.newFixedThreadPool(nbThreads);
        CountDownLatch pret = new CountDownLatch(nbThreads);
        CountDownLatch depart = new CountDownLatch(1);
        List<Integer> statuts = new java.util.concurrent.CopyOnWriteArrayList<>();
        AtomicInteger erreursInattendues = new AtomicInteger(0);

        for (int i = 0; i < nbThreads; i++) {
            executor.submit(() -> {
                try {
                    pret.countDown();
                    depart.await();
                    int statut = mockMvc.perform(post("/api/presences")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content("{\"code\":\"" + codeValide + "\",\"etudiantId\":2}"))
                            .andReturn().getResponse().getStatus();
                    statuts.add(statut);
                } catch (Exception e) {
                    erreursInattendues.incrementAndGet();
                }
            });
        }

        pret.await();
        depart.countDown();
        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);

        assertThat(erreursInattendues.get()).isZero();
        assertThat(statuts).hasSize(2);
        assertThat(statuts).containsExactlyInAnyOrder(201, 409);
    }
}
