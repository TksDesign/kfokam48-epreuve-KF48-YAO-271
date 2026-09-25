-- Donnees de demonstration, chargees au demarrage (exigence "demarrage" du bareme)

INSERT INTO promotion (id, nom) VALUES (1, 'Promotion Demo KFOKAM48');

INSERT INTO etudiant (id, nom, promotion_id) VALUES
    (1, 'Amina Ngo',       1),
    (2, 'Brice Talla',     1),
    (3, 'Carine Mballa',   1),
    (4, 'David Fotso',     1),
    (5, 'Estelle Nkeng',   1);

-- Relance les sequences apres insertion d'ids explicites
SELECT setval('promotion_id_seq', (SELECT MAX(id) FROM promotion));
SELECT setval('etudiant_id_seq', (SELECT MAX(id) FROM etudiant));
