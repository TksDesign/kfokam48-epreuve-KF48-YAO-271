package com.kfokam48.presencerelecture.web.dto;

/**
 * EF5 : un exercice vu par son auteur. `note`/`commentaire` nulls tant que
 * non evalue. Ne contient jamais l'identite du relecteur (RG10).
 */
public record MonExerciceResponse(Long id, String lien, String statut, Integer note, String commentaire) {
}
