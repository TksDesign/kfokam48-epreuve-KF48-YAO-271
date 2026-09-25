package com.kfokam48.presencerelecture.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "presence")
public class Presence {

    public enum Source { ETUDIANT, FORMATEUR }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Instant date;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Source source;

    @Column(name = "session_id", nullable = false)
    private Long sessionId;

    @Column(name = "etudiant_id", nullable = false)
    private Long etudiantId;

    protected Presence() {
        // JPA
    }

    public Presence(Instant date, Source source, Long sessionId, Long etudiantId) {
        this.date = date;
        this.source = source;
        this.sessionId = sessionId;
        this.etudiantId = etudiantId;
    }

    public Long getId() {
        return id;
    }

    public Instant getDate() {
        return date;
    }

    public Source getSource() {
        return source;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public Long getEtudiantId() {
        return etudiantId;
    }
}
