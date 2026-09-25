-- Schema initial, calque sur docs/diagrammes/D2.md

CREATE TABLE promotion (
    id   BIGSERIAL PRIMARY KEY,
    nom  VARCHAR(120) NOT NULL
);

CREATE TABLE etudiant (
    id            BIGSERIAL PRIMARY KEY,
    nom           VARCHAR(120) NOT NULL,
    promotion_id  BIGINT NOT NULL REFERENCES promotion (id)
);

CREATE TABLE session (
    id             BIGSERIAL PRIMARY KEY,
    titre          VARCHAR(200) NOT NULL,
    code           VARCHAR(20)  NOT NULL UNIQUE,
    ouverture_at   TIMESTAMP NOT NULL,
    expiration_at  TIMESTAMP NOT NULL,
    cloturee       BOOLEAN NOT NULL DEFAULT FALSE,
    promotion_id   BIGINT NOT NULL REFERENCES promotion (id)
);

-- RG1 : recherche rapide par code
CREATE INDEX idx_session_code ON session (code);

CREATE TABLE presence (
    id           BIGSERIAL PRIMARY KEY,
    date         TIMESTAMP NOT NULL DEFAULT now(),
    source       VARCHAR(10) NOT NULL CHECK (source IN ('ETUDIANT', 'FORMATEUR')),
    session_id   BIGINT NOT NULL REFERENCES session (id),
    etudiant_id  BIGINT NOT NULL REFERENCES etudiant (id),
    -- RG : un etudiant ne peut etre present qu'une fois par session (409 DEJA_PRESENT)
    CONSTRAINT uq_presence_session_etudiant UNIQUE (session_id, etudiant_id)
);

CREATE TABLE exercice (
    id            BIGSERIAL PRIMARY KEY,
    lien          VARCHAR(500) NOT NULL,
    statut        VARCHAR(20) NOT NULL CHECK (statut IN ('DEPOSE', 'EN_ATTENTE', 'EVALUE')),
    session_id    BIGINT NOT NULL REFERENCES session (id),
    auteur_id     BIGINT NOT NULL REFERENCES etudiant (id),
    relecteur_id  BIGINT REFERENCES etudiant (id),
    -- RG : un seul exercice par etudiant et par session (409 EXERCICE_DEJA_DEPOSE)
    CONSTRAINT uq_exercice_session_auteur UNIQUE (session_id, auteur_id),
    -- RG3 : un etudiant ne peut pas relire son propre exercice
    CONSTRAINT chk_pas_auto_relecture CHECK (relecteur_id IS NULL OR relecteur_id <> auteur_id)
);

CREATE TABLE relecture (
    id           BIGSERIAL PRIMARY KEY,
    note         INTEGER NOT NULL CHECK (note BETWEEN 0 AND 20),
    commentaire  TEXT NOT NULL,
    date         TIMESTAMP NOT NULL DEFAULT now(),
    -- RG9 : un seul relecteur/une seule relecture par exercice
    exercice_id  BIGINT NOT NULL UNIQUE REFERENCES exercice (id)
);
