package com.kfokam48.presencerelecture.web.dto;

/**
 * Format d'erreur impose par api/contrat.yaml, pour toutes les erreurs sans exception.
 */
public record ErreurDTO(String code, String message) {
}
