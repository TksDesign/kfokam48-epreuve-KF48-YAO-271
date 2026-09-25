# Backlog du Projet (Issues GitHub à créer)

*Note : Ces éléments doivent être créés sous forme d'Issues sur le dépôt GitHub. Le barème attribue 8 points à ces issues.*

---

## 1. Un formateur peut démarrer l'application avec des données prêtes à l'emploi
**Priorité :** Must
**Description :** Mettre en place le projet Spring Boot avec les migrations (schéma initial basé sur D2) et un script de peuplement (ex: `import.sql` ou `data.sql` Flyway) insérant au moins une promotion et quelques étudiants de test.
**Critères d'acceptation :**
- L'application démarre sans erreur via `mvnw spring-boot:run` ou `docker compose up`.
- Des données de base (promotions, étudiants) sont accessibles sans manipulation supplémentaire.
**Réf :** B1, B5, Démarrage (3 pts)

## 2. API : Ouvrir et clôturer une session formateur
**Priorité :** Must
**Description :** Créer les endpoints `POST /api/sessions` et `PUT /api/sessions/{id}/cloture`.
**Critères d'acceptation :**
- La création génère un code et retourne un HTTP 201 avec `id, code, ouvertureAt, expirationAt` (EF2).
- La clôture d'une session retourne HTTP 200.
**Réf :** EF2, B2

## 3. API : Marquer sa présence
**Priorité :** Must
**Description :** Créer l'endpoint `POST /api/presences`.
**Critères d'acceptation :**
- Accepte un code valide : HTTP 201 avec source "ETUDIANT" (EF1).
- Rejette un code expiré (> 15 min) avec HTTP 410 (RG1).
- Rejette si l'étudiant est déjà présent avec HTTP 409.
- Retourne HTTP 400 si le code est inconnu.
**Réf :** EF1, RG1, B2

## 4. API : Ajouter une présence manuellement par le formateur
**Priorité :** Should
**Description :** Créer l'endpoint `POST /api/sessions/{id}/presences-manuelles`.
**Critères d'acceptation :**
- Accepte un étudiant (sans code) et retourne HTTP 201 avec source "FORMATEUR".
- Rejette si l'étudiant est déjà présent avec HTTP 409.
**Réf :** EF7, RG8

## 5. API : Déposer un exercice
**Priorité :** Must
**Description :** Créer l'endpoint `POST /api/exercices`.
**Critères d'acceptation :**
- Crée un exercice avec un statut "DEPOSE" (HTTP 201).
- Rejette les URLs invalides avec HTTP 400.
- Rejette si l'exercice est déjà déposé avec HTTP 409.
- Assigne automatiquement un relecteur au hasard parmi les étudiants présents
  à la session, hors l'auteur (EF4, RG4/Q7). Statut passe à `EN_ATTENTE` si un
  relecteur a pu être assigné, reste `DEPOSE` sinon.
**Réf :** EF3, EF4, RG4, RG11, B2

*Ajouté a posteriori (25/09) : EF4 n'était dans aucun ticket malgré Must au
cahier des charges — repéré en préparant ce ticket.*

## 6. API : Evaluer un exercice (Relecture)
**Priorité :** Must
**Description :** Créer l'endpoint `POST /api/relectures/{id}` (`{id}` = id de
l'exercice relu).
**Critères d'acceptation :**
- Enregistre une note entière entre 0 et 20 (HTTP 200) (RG5).
- Renvoie HTTP 403 si `relecteurId` n'est pas le relecteur assigné (RG3, EF4).
- Renvoie HTTP 409 si une relecture est déjà rendue (RG7).
- Renvoie HTTP 400 si la note est hors borne.
- Ne doit pas associer plus d'un relecteur au même exercice (RG9).
- Statut de l'exercice passe à EVALUE.
**Réf :** EF5, RG3, RG5, RG7, RG9, B2

*`relecteurId` ajouté au corps (25/09) : absent du contrat imposé, nécessaire
pour vérifier RG3 sans authentification. Voir CAHIER_DES_CHARGES.md §7.*

## 7. API : Consulter le tableau récapitulatif
**Priorité :** Must
**Description :** Créer l'endpoint `GET /api/tableau`.
**Critères d'acceptation :**
- Retourne la liste des étudiants avec leurs présences, exercices déposés, moyenne (calculée) et relectures en attente (HTTP 200).
- Renvoie HTTP 404 si la promotion est inconnue.
**Réf :** EF6, B2

## 8. L'utilisateur comprend pourquoi son action a échoué (messages clairs)
**Priorité :** Must
**Description :** Implémenter un `@RestControllerAdvice` dans le backend.
**Critères d'acceptation :**
- Toute exception métier est attrapée et formatée selon `{ "code": "...", "message": "..." }`.
- Aucune stack trace n'est visible en HTTP.
**Réf :** B4

## 9. Front-End : Écran Étudiant (Présence & Dépôt & Vue Évaluation)
**Priorité :** Must
**Description :** Créer l'interface permettant à un étudiant de sélectionner son nom, de saisir un code pour être présent, de déposer le lien de son exercice, et de voir sa note.
**Critères d'acceptation :**
- Les appels API sont faits dans une couche service dédiée (F3).
- Les états de chargement et d'erreurs (ex: Code expiré) sont affichés à l'utilisateur.
- L'identité du relecteur n'est jamais affichée (RG10).
**Réf :** F2, F3, EF5, RG10

## 10. Front-End : Écran Formateur (Tableau de bord)
**Priorité :** Must
**Description :** Interface pour le formateur avec création de session, ajout manuel et visualisation du tableau.
**Critères d'acceptation :**
- Affiche le code de session et sa validité (15 minutes).
- Récupère et affiche la grille `GET /api/tableau`.
- Permet l'ajout de présence manuel.
- Ne recalcule pas la moyenne en local (logique laissée au backend).
**Réf :** F2, F3, EF7

## 11. Front-End : Écran Relecteur (Évaluation)
**Priorité :** Must
**Description :** Interface pour l'étudiant assigné comme relecteur.
**Critères d'acceptation :**
- Permet la saisie d'une note (0-20) et d'un commentaire.
- Gère les cas d'erreur de relecture (ex: déjà soumis).
**Réf :** F2, F3
