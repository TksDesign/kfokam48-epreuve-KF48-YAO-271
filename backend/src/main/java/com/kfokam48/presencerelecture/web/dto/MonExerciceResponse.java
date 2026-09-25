package com.kfokam48.presencerelecture.web.dto;

/**
 * EF5 : un exercice vu par son auteur. `note`/`commentaire` nulls tant que
 * non evalue. Ne contient jamais l'identite du relecteur (RG10).
 *
 * RG12/RG13 (etape 3) : `note` est la moyenne des 2 relectures si les 2 sont
 * rendues, ou la note du seul relecteur ayant rendu (`provisoire=true`).
 */
public record MonExerciceResponse(Long id, String lien, String statut, Double note, String commentaire, boolean provisoire) {
}
