package com.kfokam48.presencerelecture.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "session")
public class Session {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titre;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(name = "ouverture_at", nullable = false)
    private Instant ouvertureAt;

    @Column(name = "expiration_at", nullable = false)
    private Instant expirationAt;

    @Column(nullable = false)
    private boolean cloturee = false;

    @Column(name = "promotion_id", nullable = false)
    private Long promotionId;

    protected Session() {
        // JPA
    }

    public Session(String titre, String code, Instant ouvertureAt, Instant expirationAt, Long promotionId) {
        this.titre = titre;
        this.code = code;
        this.ouvertureAt = ouvertureAt;
        this.expirationAt = expirationAt;
        this.promotionId = promotionId;
    }

    public Long getId() {
        return id;
    }

    public String getTitre() {
        return titre;
    }

    public String getCode() {
        return code;
    }

    public Instant getOuvertureAt() {
        return ouvertureAt;
    }

    public Instant getExpirationAt() {
        return expirationAt;
    }

    public boolean isCloturee() {
        return cloturee;
    }

    public Long getPromotionId() {
        return promotionId;
    }

    /** RG1 : le code n'est plus valable 15 minutes apres l'ouverture. */
    public boolean estExpiree(Instant maintenant) {
        return maintenant.isAfter(expirationAt);
    }

    public void cloturer() {
        this.cloturee = true;
    }
}
