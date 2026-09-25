package com.kfokam48.presencerelecture.web.error;

import com.kfokam48.presencerelecture.domain.exception.CodeExpireException;
import com.kfokam48.presencerelecture.web.dto.ErreurDTO;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void mappe_une_exception_metier_vers_le_format_du_contrat() {
        ResponseEntity<ErreurDTO> reponse = handler.handleApiException(new CodeExpireException());

        assertThat(reponse.getStatusCode()).isEqualTo(HttpStatus.GONE);
        assertThat(reponse.getBody().code()).isEqualTo("CODE_EXPIRE");
        assertThat(reponse.getBody().message()).isNotBlank();
    }

    @Test
    void ne_laisse_jamais_fuir_le_detail_d_une_exception_inattendue() {
        ResponseEntity<ErreurDTO> reponse = handler.handleUnexpected(new RuntimeException("detail interne sensible"));

        assertThat(reponse.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(reponse.getBody().code()).isEqualTo("ERREUR_INTERNE");
        assertThat(reponse.getBody().message()).doesNotContain("detail interne sensible");
    }
}
