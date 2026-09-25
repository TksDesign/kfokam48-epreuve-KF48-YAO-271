package com.kfokam48.presencerelecture.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.hamcrest.Matchers.hasLength;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test d'integration : demarre un vrai Postgres ephemere via Testcontainers,
 * aucune base locale requise (B6). Verifie EF2 et le format du contrat.
 */
@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
class SessionControllerIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private MockMvc mockMvc;

    @Test
    void ouvre_une_session_et_retourne_le_format_impose_par_le_contrat() throws Exception {
        mockMvc.perform(post("/api/sessions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"titre\":\"Cours Spring Boot\",\"promotionId\":1}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(notNullValue()))
                .andExpect(jsonPath("$.code", hasLength(6)))
                .andExpect(jsonPath("$.ouvertureAt").value(notNullValue()))
                .andExpect(jsonPath("$.expirationAt").value(notNullValue()));
    }

    @Test
    void refuse_l_ouverture_sans_titre_avec_400() throws Exception {
        mockMvc.perform(post("/api/sessions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"promotionId\":1}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERREUR"));
    }

    @Test
    void cloture_une_session_puis_refuse_une_seconde_cloture() throws Exception {
        String reponse = mockMvc.perform(post("/api/sessions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"titre\":\"A cloturer\",\"promotionId\":1}"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        Long id = Long.valueOf(reponse.replaceAll(".*\"id\":(\\d+).*", "$1"));

        mockMvc.perform(put("/api/sessions/{id}/cloture", id))
                .andExpect(status().isOk());

        mockMvc.perform(put("/api/sessions/{id}/cloture", id))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("DEJA_CLOTUREE"));
    }

    @Test
    void cloturer_une_session_inconnue_retourne_404() throws Exception {
        mockMvc.perform(put("/api/sessions/{id}/cloture", 999_999))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("SESSION_INCONNUE"));
    }
}
