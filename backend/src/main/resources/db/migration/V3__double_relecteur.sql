-- RG12/RG13 (enveloppe etape 3, issues #38-#40) : un exercice est desormais
-- relu par 2 relecteurs distincts au lieu d'1 seul. V1/V2 non modifiees.

-- Deuxieme relecteur assigne, en plus de relecteur_id (le premier).
ALTER TABLE exercice ADD COLUMN relecteur2_id BIGINT REFERENCES etudiant (id);

ALTER TABLE exercice ADD CONSTRAINT chk_pas_auto_relecture2
    CHECK (relecteur2_id IS NULL OR relecteur2_id <> auteur_id);

ALTER TABLE exercice ADD CONSTRAINT chk_relecteurs_distincts
    CHECK (relecteur2_id IS NULL OR relecteur_id IS NULL OR relecteur2_id <> relecteur_id);

-- La relecture doit maintenant savoir QUI l'a rendue : jusqu'ici jamais
-- necessaire (un seul relecteur possible par exercice), la contrainte
-- unique portait directement sur exercice_id.
ALTER TABLE relecture ADD COLUMN relecteur_id BIGINT REFERENCES etudiant (id);

-- Backfill : pour toute relecture existante, le relecteur au moment du
-- depot etait forcement exercice.relecteur_id (ancien modele = 1 seul).
UPDATE relecture r
SET relecteur_id = e.relecteur_id
FROM exercice e
WHERE e.id = r.exercice_id
  AND r.relecteur_id IS NULL;

ALTER TABLE relecture ALTER COLUMN relecteur_id SET NOT NULL;

-- Remplace l'ancienne contrainte "1 relecture par exercice" par
-- "1 relecture par exercice ET par relecteur" (2 relecteurs -> 2 possibles).
ALTER TABLE relecture DROP CONSTRAINT IF EXISTS relecture_exercice_id_key;
ALTER TABLE relecture ADD CONSTRAINT uq_relecture_exercice_relecteur UNIQUE (exercice_id, relecteur_id);
