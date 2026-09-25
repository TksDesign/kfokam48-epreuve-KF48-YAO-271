package com.kfokam48.presencerelecture.web.error;

import com.kfokam48.presencerelecture.domain.exception.ApiException;
import com.kfokam48.presencerelecture.web.dto.ErreurDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Point unique de gestion des erreurs (B4). Toute reponse d'erreur suit le
 * format impose par api/contrat.yaml : { "code": "...", "message": "..." }.
 * Aucune stack trace, aucun corps vide, jamais la page Whitelabel de Spring.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErreurDTO> handleApiException(ApiException ex) {
        return ResponseEntity.status(ex.getHttpStatus())
                .body(new ErreurDTO(ex.getCode(), ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErreurDTO> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(fieldError -> fieldError.getField() + " : " + fieldError.getDefaultMessage())
                .orElse("Requete invalide.");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErreurDTO("VALIDATION_ERREUR", message));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErreurDTO> handleUnexpected(Exception ex) {
        log.error("Erreur inattendue", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErreurDTO("ERREUR_INTERNE", "Une erreur inattendue est survenue."));
    }
}
