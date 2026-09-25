package com.kfokam48.presencerelecture.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "relecture")
public class Relecture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer note;

    @Column(nullable = false)
    private String commentaire;

    @Column(nullable = false)
    private Instant date;

    @Column(name = "exercice_id", nullable = false, unique = true)
    private Long exerciceId;

    protected Relecture() {
        // JPA
    }

    public Relecture(Integer note, String commentaire, Instant date, Long exerciceId) {
        this.note = note;
        this.commentaire = commentaire;
        this.date = date;
        this.exerciceId = exerciceId;
    }

    public Long getId() {
        return id;
    }

    public Integer getNote() {
        return note;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public Instant getDate() {
        return date;
    }

    public Long getExerciceId() {
        return exerciceId;
    }
}
