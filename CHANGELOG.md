# CHANGELOG

## [v1.0] — 2026-09-25 (étape 3/4 — enveloppe)

### Corrigé
- Race condition sur `POST /api/presences` : deux requêtes quasi simultanées pour le même étudiant/session pouvaient produire un `500` au lieu d'un `409 DEJA_PRESENT` propre (#37).
- `POST /api/sessions/{id}/presences-manuelles` implémenté (#4) — endpoint existait au contrat depuis l'étape 1 mais jamais codé côté backend.

### Ajouté
- **Double relecteur** (RG12/RG13, remplace RG9) : chaque exercice est désormais relu par 2 pairs distincts, la note retenue est la moyenne des deux ; si un seul a rendu, sa note est affichée comme provisoire (#38, #39, #40).
- Migration `V3__double_relecteur.sql` — `exercice.relecteur2_id`, `relecture.relecteur_id`.
- Service `frontend` ajouté à `docker-compose.yml` (Dockerfile multi-stage, absent depuis l'étape 2).

### Modifié
- `GET /api/exercices?etudiantId=` : `note` devient la moyenne des 2 relectures (ou note unique + `provisoire=true` si une seule rendue).
- `POST /api/relectures/{id}` : le `409 RELECTURE_DEJA_RENDUE` se décide désormais par relecteur, plus par exercice.

## [v0.1] — 2026-09-25 (étape 2 — première version)

### Ajouté
- Backend Spring Boot : sessions (ouverture/clôture, code à expiration 15 min), présence par code, blocage après 5 codes inconnus, dépôt d'exercice avec assignation aléatoire d'un relecteur parmi les présents, relecture (note + commentaire), tableau récapitulatif formateur.
- Gestion centralisée des erreurs (`@RestControllerAdvice`), migrations Flyway (schéma + données de démo), Testcontainers pour les tests d'intégration.
- Frontend React : 3 écrans (Formateur, Étudiant, Relecteur) + accueil de sélection du rôle, couche API dédiée avec bascule mocks/réel par variables d'environnement.
- `GET /api/exercices?etudiantId=` (EF5, l'étudiant consulte sa propre note).
- Contrat étendu : `PUT /sessions/{id}/cloture`, `POST /sessions/{id}/presences-manuelles`, `429 TROP_DE_TENTATIVES`, `relecteurId` sur les relectures.
- Protection de branche GitHub sur `main` (PR obligatoire, force-push interdit).

### Corrigé
- CORS absent, bloquant toute requête réelle navigateur → backend — trouvé lors du premier test en conditions réelles (#32).
- Écran Relecteur (liste d'exercices figée en dur) et écran Étudiant (note jamais affichée) reconnectés aux vrais endpoints.

## [analyse] — 2026-09-25 (étape 1)

### Ajouté
- Cahier des charges (7 exigences fonctionnelles, 11 règles de gestion), 4 diagrammes Mermaid, contrat d'API complété (+2 endpoints), backlog en 11 issues GitHub priorisées.
