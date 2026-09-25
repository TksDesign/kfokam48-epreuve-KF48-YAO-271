# Cahier des charges — Présence & Relecture

**Auteur :** Tissie Kuate Shanonn Daphanne · KF48-YAO-271
**Version :** 1 · **Date :** 25 septembre 2026
**Frontend choisi :** React, parce qu'il offre un écosystème riche et permet un développement rapide et déclaratif des trois écrans requis.

---

## 1. Contexte et objectif

La direction de la formation KFOKAM48 a besoin d'une application de suivi pour gérer l'interaction en direct lors de ses sessions de cours. L'objectif est de permettre aux formateurs d'ouvrir des sessions, aux étudiants de marquer leur présence, de déposer leurs exercices et d'effectuer des relectures par les pairs. Cette digitalisation remplacera les appels manuels et permettra de responsabiliser les étudiants à travers la correction croisée, le tout avec un tableau de bord en temps réel pour le formateur.

## 2. Acteurs et rôles

| Acteur | Ce qu'il peut faire | Ce qu'il ne peut pas faire |
|---|---|---|
| Formateur | Ouvrir et clôturer une session, voir le tableau récapitulatif, ajouter une présence manuellement. | Déposer un exercice, relire un exercice étudiant. |
| Étudiant | Marquer sa présence via un code, déposer le lien de son exercice, remplacer son lien avant relecture. | Relire son propre exercice, modifier une relecture déjà envoyée. |
| Relecteur | (C'est un Étudiant assigné au hasard) Évaluer (note 0-20) et commenter l'exercice d'un pair. | Relire son propre exercice, modifier sa note une fois validée. |

*Le relecteur n'est pas un acteur distinct, c'est un Étudiant dans l'état "assigné à un exercice tiers". Cela implique que l'entité Étudiant gère à la fois ses propres exercices et ses relectures.*

## 3. Périmètre

**Inclus dans cette version :**
- Suivi des présences par code à durée de vie limitée (15 min).
- Dépôt de liens (URI) pour les exercices.
- Assignation aléatoire d'un relecteur (parmi les présents).
- Évaluation par les pairs (note + commentaire).
- Tableau de bord formateur (agrégation des données en temps réel).

**Explicitement exclu :**
- L'authentification complexe avec mot de passe (les étudiants se choisissent dans une liste, Q1).
- La gestion des fichiers d'exercices (on ne gère que les liens).
- L'envoi d'e-mails ou de notifications externes.
- La modification d'une note de relecture après soumission (choix Q15).

## 4. Exigences fonctionnelles

| Réf | Exigence | Critère d'acceptation | Priorité |
|---|---|---|---|
| EF1 | L'étudiant marque sa présence à l'aide d'un code | Quand je saisis un code valide et non expiré, ma présence apparaît dans le tableau du formateur | Must |
| EF2 | Le formateur ouvre une session | Quand le formateur donne un titre et choisit une promotion, l'app génère un code de présence | Must |
| EF3 | L'étudiant dépose un lien d'exercice | Quand un étudiant dépose une URL valide, l'exercice est marqué comme déposé | Must |
| EF4 | Le système assigne un relecteur | Quand un exercice est déposé, un étudiant *présent* autre que l'auteur est désigné aléatoirement | Must |
| EF5 | L'étudiant relu voit son évaluation | Quand la relecture est envoyée, l'auteur voit la note et le commentaire, mais pas le nom du relecteur | Must |
| EF6 | Le formateur voit le tableau récapitulatif | Quand le formateur consulte le tableau, il voit les présences, la moyenne et les relectures en attente | Must |
| EF7 | Le formateur ajoute une présence manuellement | Quand un étudiant a un problème technique, le formateur peut forcer sa présence (sans code) | Should |

## 5. Exigences non fonctionnelles

| Réf | Exigence | Comment on la vérifie |
|---|---|---|
| ENF1 | L'interface est réactive et utilisable sur mobile | L'écran étudiant (présence + dépôt) ne nécessite pas de scroll horizontal sur un écran de 320px |
| ENF2 | Les API respectent exactement le contrat (B2) | Le script d'évaluation backend s'exécute sans erreur 404/400 sur les endpoints du contrat |
| ENF3 | Le backend centralise la gestion des erreurs (B4) | Aucun appel API ne retourne de stack trace Java, mais un JSON avec `code` et `message` |

## 6. Règles de gestion

| Réf | Règle | Source |
|---|---|---|
| RG1 | Un code de présence expire 15 minutes après l'ouverture de la session | Q2 |
| RG2 | Au bout de 5 erreurs de code, l'étudiant est bloqué 2 minutes | Q4 |
| RG3 | Un étudiant ne peut pas relire son propre exercice | Q5 |
| RG4 | L'assignation d'une relecture se fait au hasard parmi les *présents* | Q7 |
| RG5 | Une note est un entier compris entre 0 et 20 | Q9 |
| RG6 | Un étudiant peut remplacer son lien tant que la relecture n'est pas commencée | Q13 |
| RG7 | Une relecture envoyée est définitive | Q15 |
| RG8 | Une présence peut être ajoutée manuellement par le formateur (source=FORMATEUR) | Q14 |
| RG9 | Un exercice est corrigé par un et un seul relecteur | Q6 |
| RG10 | L'identité du relecteur est anonyme (masquée) pour l'étudiant évalué | Q8 |
| RG11 | Le dépôt d'un exercice reste possible jusqu'à la clôture de la session (même après expiration du code) | Q12 |

## 7. Zones d'ombre, hypothèses et contradictions

**Points que la demande ne tranche pas :**

| Point | Réponse client (Qx) ou hypothèse | Décision retenue | Conséquence |
|---|---|---|---|
| Assignation s'il n'y a pas assez de présents | Hypothèse : Si un étudiant est le seul présent ou le premier à déposer, il ne peut être relu de suite. | L'assignation est faite au moment du dépôt *s'il y a des candidats valides*. Sinon, elle est mise en file d'attente. | Un exercice peut rester sans relecteur temporairement. |
| Clôture de la session | Q10/Q12 mentionnent "clôturer la session", mais l'API ne l'inclut pas. | Nous rajoutons un endpoint custom `PUT /api/sessions/{id}/cloture`. | Ajout au contrat d'API. |
| Présence manuelle (Le "trou") | Q14 demande un ajout manuel, mais la méthode n'est pas spécifiée dans l'API imposée initiale pour le formateur. | On ajoute une route `POST /api/sessions/{id}/presences-manuelles` permettant au formateur d'ajouter un étudiant sans code (source=FORMATEUR). | Ajout au contrat d'API. |
| Ambiguïté de `relecturesEnAttente` | Q16 : "les relectures qu'il doit encore faire". Est-ce celles où il est relecteur ou celles sur son exercice ? | Il s'agit du nombre d'exercices tiers qui lui ont été assignés et qu'il n'a pas encore évalués. | Détermine la requête SQL sous-jacente du GET /api/tableau. |

**Contradictions relevées :**

| Réponses en conflit | Ce que j'ai choisi | Pourquoi |
|---|---|---|
| Q10 (modification possible) vs Q15 (définitive) | Je tranche en faveur de Q15 (note définitive). | Le contrat d'API imposé (`POST /api/relectures/{id}`) ne propose pas de `PUT/PATCH` et retourne l'erreur `409 RELECTURE_DEJA_RENDUE`. Cela corrobore Q15 techniquement. |

## 8. Contraintes techniques

- **Backend (B1-B6)** : Java 17+, Maven (wrapper commité). Contrat `api/contrat.yaml` scrupuleusement respecté. Architecture en 3 couches (Controller/Service/Repository) et pattern DTO imposé. `@RestControllerAdvice` pour les erreurs. Base de données versionnée avec Flyway/Liquibase (migrations incluses). Tests unitaires métier et d'intégration HTTP.
- **Frontend (F1-F3)** : React déclaré dans le README. 3 écrans (Formateur, Étudiant, Relecteur). Appels API centralisés, états de chargement gérés, aucune logique métier redondante dans le client (ex: la moyenne vient du backend).
- **Démarrage** : via `docker compose up` ou un README clair, avec des données préchargées.

## 9. Livrables

- Le dépôt GitHub avec l'historique complet des commits (`[JALON] analyse`, `[JALON] v0.1`, `[JALON] v1.0`).
- Documentation (Cahier des charges, 4 diagrammes, contrats d'API, BACKLOG.md).
- Code source Frontend (React) et Backend (Spring Boot).
- Dépôt de l'épreuve Git (`kfokam48-gitlab-...`).
- Fichier de soumission (`SOUMISSION.md`) déposé sur la plateforme.

## 10. Démarche prévue

1. **Étape 1 :** Analyse détaillée, formalisation de ce document, réalisation des 4 diagrammes (Cas d'utilisation, Classes, Séquence, États). Mise à jour du contrat API. Création des issues (Backlog). Validation puis `[JALON] analyse`.
2. **Étape 2 :** Développement en parallèle du Frontend et du Backend (version initiale). Passage des tests et respect strict des contrats d'API pour l'intégration. Poussée du `[JALON] v0.1`.
3. **Étape 3 :** Ouverture de l'enveloppe, analyse des impacts, mise à jour de la documentation.
4. **Étape 4 :** Implémentation des changements de l'enveloppe et livraison du `[JALON] v1.0`.
5. **Étape 5 & 6 :** Épreuve Git indépendante, remplissage du README final, vérification à froid, et dépôt du document de soumission avant 18h00.

**Definition of Done — un ticket est terminé quand :**
- Le code compile et passe les tests unitaires.
- Le contrat d'API est respecté.
- Les erreurs sont proprement interceptées.
- Le code source est poussé sur le dépôt distant.

---

## Journal des révisions

| Version | Quand | Ce qui a changé et pourquoi |
|---|---|---|
| 1 | 25/09 | Version initiale - Rédaction post-analyse |
