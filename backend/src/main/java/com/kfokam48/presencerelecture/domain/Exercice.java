package com.kfokam48.presencerelecture.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "exercice")
public class Exercice {

    public enum Statut { DEPOSE, EN_ATTENTE, EVALUE }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String lien;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Statut statut;

    @Column(name = "session_id", nullable = false)
    private Long sessionId;

    @Column(name = "auteur_id", nullable = false)
    private Long auteurId;

    @Column(name = "relecteur_id")
    private Long relecteurId;

    @Column(name = "relecteur2_id")
    private Long relecteur2Id;

    protected Exercice() {
        // JPA
    }

    public Exercice(String lien, Long sessionId, Long auteurId) {
        this.lien = lien;
        this.statut = Statut.DEPOSE;
        this.sessionId = sessionId;
        this.auteurId = auteurId;
    }

    public Long getId() {
        return id;
    }

    public String getLien() {
        return lien;
    }

    public Statut getStatut() {
        return statut;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public Long getAuteurId() {
        return auteurId;
    }

    public Long getRelecteurId() {
        return relecteurId;
    }

    public Long getRelecteur2Id() {
        return relecteur2Id;
    }

    /** EF4/RG4 : assignation d'un relecteur au hasard parmi les presents, hors l'auteur. */
    public void assignerRelecteur(Long relecteurId) {
        this.relecteurId = relecteurId;
        this.statut = Statut.EN_ATTENTE;
    }

    /** RG12 (etape 3) : assignation de 2 relecteurs distincts au hasard parmi les presents. relecteur2 nullable si un seul candidat. */
    public void assignerRelecteurs(Long relecteur1, Long relecteur2) {
        this.relecteurId = relecteur1;
        this.relecteur2Id = relecteur2;
        this.statut = Statut.EN_ATTENTE;
    }

    /** Nombre de relecteurs reellement assignes a cet exercice (1 ou 2). */
    public int nombreRelecteursAssignes() {
        return relecteur2Id != null ? 2 : 1;
    }

    /** EF5 : la relecture a ete rendue. */
    public void marquerEvalue() {
        this.statut = Statut.EVALUE;
    }
}
