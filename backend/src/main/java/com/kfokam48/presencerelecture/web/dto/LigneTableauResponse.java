package com.kfokam48.presencerelecture.web.dto;

/**
 * Une ligne du tableau recapitulatif, format impose par le contrat.
 * `moyenne` nullable si l'etudiant n'a recu aucune relecture.
 */
public record LigneTableauResponse(
        Long etudiantId,
        String nom,
        long presences,
        long exercicesDeposes,
        Double moyenne,
        long relecturesEnAttente
) {
}
