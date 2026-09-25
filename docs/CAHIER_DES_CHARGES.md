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
| RG9 | ~~Un exercice est corrigé par un et un seul relecteur~~ — **remplacée par RG12** (enveloppe étape 3, changement de besoin) | Q6 |
| RG10 | L'identité du relecteur est anonyme (masquée) pour l'étudiant évalué | Q8 |
| RG11 | Le dépôt d'un exercice reste possible jusqu'à la clôture de la session (même après expiration du code) | Q12 |
| RG12 | Un exercice est relu par **deux** relecteurs distincts (parmi les présents, hors auteur). La note retenue est la **moyenne** des deux notes. | Q6 (révisé, enveloppe étape 3) |
| RG13 | Si un seul des deux relecteurs a rendu sa note, elle est affichée à l'étudiant comme **provisoire** (peut encore changer) | Enveloppe étape 3 |

> **RG9 → RG12/RG13 (25/09, étape 3) :** le client est revenu après le premier test avec un changement de besoin cassant RG9 (« un seul relecteur ça ne marche pas : quand il ne rend rien, l'étudiant n'a aucune note »). Voir section 7 pour l'analyse d'impact complète.

## 7. Zones d'ombre, hypothèses et contradictions

**Points que la demande ne tranche pas :**

| Point | Réponse client (Qx) ou hypothèse | Décision retenue | Conséquence |
|---|---|---|---|
| Assignation s'il n'y a pas assez de présents | Hypothèse : Si un étudiant est le seul présent ou le premier à déposer, il ne peut être relu de suite. | L'assignation est faite au moment du dépôt *s'il y a des candidats valides*. Sinon, elle est mise en file d'attente. | Un exercice peut rester sans relecteur temporairement. |
| Clôture de la session | Q10/Q12 mentionnent "clôturer la session", mais l'API ne l'inclut pas. | Nous rajoutons un endpoint custom `PUT /api/sessions/{id}/cloture`. | Ajout au contrat d'API. |
| Présence manuelle (Le "trou") | Q14 demande un ajout manuel, mais la méthode n'est pas spécifiée dans l'API imposée initiale pour le formateur. | On ajoute une route `POST /api/sessions/{id}/presences-manuelles` permettant au formateur d'ajouter un étudiant sans code (source=FORMATEUR). | Ajout au contrat d'API. |
| Ambiguïté de `relecturesEnAttente` | Q16 : "les relectures qu'il doit encore faire". Est-ce celles où il est relecteur ou celles sur son exercice ? | Il s'agit du nombre d'exercices tiers qui lui ont été assignés et qu'il n'a pas encore évalués. | Détermine la requête SQL sous-jacente du GET /api/tableau. |
| Identité du relecteur absente de `POST /api/relectures/{id}` | Le contrat imposé ne contient que `{note, commentaire}` — aucun moyen de vérifier RG3 (auto-relecture interdite) sans authentification (Q1). | Ajout de `relecteurId` au corps, requis. Même pattern que les autres endpoints (identité explicite, pas de session). | Ajout au contrat d'API. |
| EF5 sans endpoint ("l'étudiant relu voit son évaluation", Q8) | Aucune des 5 opérations imposées ne permet à un étudiant de consulter sa propre note. `GET /api/tableau` est la vue formateur (toute la promotion), pas adaptée. Trou repéré après coup, en relisant le frontend (issue #26). | Ajout de `GET /api/exercices?etudiantId=`, sans jamais exposer `relecteurId` (RG10). | Ajout au contrat d'API. |
| File d'attente jamais implémentée (ligne ci-dessus) | Repéré en testant le rôle relecteur en conditions réelles (étape 3, enveloppe) : un exercice déposé sans candidat présent reste `DEPOSE` sans relecteur **indéfiniment** — rien ne retente l'assignation plus tard, contrairement à ce que "mise en file d'attente" laissait entendre. | Ne pas corriger isolément : la logique d'assignation est de toute façon réécrite par le changement RG6 (double relecteur, étape 3). Traité une seule fois, dans ce chantier-là. | Absorbé dans le refactor RG6 ; pas d'issue/branche dédiée pour ce point seul. |

**Contradictions relevées :**

| Réponses en conflit | Ce que j'ai choisi | Pourquoi |
|---|---|---|
| Q10 (modification possible) vs Q15 (définitive) | Je tranche en faveur de Q15 (note définitive). | Le contrat d'API imposé (`POST /api/relectures/{id}`) ne propose pas de `PUT/PATCH` et retourne l'erreur `409 RELECTURE_DEJA_RENDUE`. Cela corrobore Q15 techniquement. |

## 7bis. Changement de besoin — étape 3 (enveloppe, 25/09)

**Demande client (verbatim) :** « Finalement, un seul relecteur ça ne marche pas : quand il ne rend rien, l'étudiant n'a aucune note. À partir de maintenant, chaque exercice est relu par deux pairs différents, et la note retenue est la moyenne des deux. Si un seul des deux a rendu, on affiche sa note en attendant, mais marquée comme provisoire. »

**Règle cassée :** RG9 (« un et un seul relecteur »), issue de Q6. Remplacée par RG12 + RG13 (section 6).

**Impact identifié :**
| Zone | Avant | Après |
|---|---|---|
| Modèle de données | `exercice.relecteur_id` (1 seul, nullable) | `exercice.relecteur_id` + `exercice.relecteur2_id` (2 candidats distincts, hors auteur) |
| Table `relecture` | `UNIQUE(exercice_id)` — 1 seule relecture possible, **pas de colonne `relecteur_id` persistée** (jamais nécessaire tant qu'il n'y en avait qu'un) | `UNIQUE(exercice_id, relecteur_id)` — 2 relectures possibles, `relecteur_id` ajouté et rempli (obligatoire pour savoir laquelle des 2 notes appartient à qui) |
| Statut d'un exercice | `EVALUE` dès la 1ʳᵉ (et unique) relecture rendue | `EVALUE` seulement quand les 2 relectures sont rendues ; 1 seule rendue ⇒ reste `EN_ATTENTE` mais une note **provisoire** est déjà consultable |
| `GET /api/exercices?etudiantId=` | `note` = note unique ou `null` | `note` = moyenne si 2 rendues, sinon note du seul relecteur ayant rendu (`provisoire=true`), sinon `null` |
| `POST /api/relectures/{id}` | 409 `RELECTURE_DEJA_RENDUE` dès qu'une relecture existe pour l'exercice | 409 seulement si **ce relecteur précis** a déjà rendu (l'autre relecteur peut encore rendre la sienne) |
| RG4 (assignation aléatoire) | 1 tirage parmi les présents | 2 tirages sans remise parmi les présents (si 1 seul présent candidat : assignation partielle, comportement RG13 s'applique dès le départ) |
| Gap "file d'attente" (section 7, ligne ci-dessus) | Jamais implémenté, documenté comme dette | Traité dans ce chantier : la ré-écriture de l'assignation est l'occasion de ne pas réintroduire le même trou côté "2ᵉ relecteur manquant" — reste néanmoins **hors périmètre v1.0** pour la ré-assignation différée (voir sacrifice ci-dessous) |

**Ce qui sort du périmètre pour absorber ce changement (Must tardif) :**
- **Ré-assignation différée** (assigner un relecteur manquant quand un nouvel étudiant marque présence après coup) : reste non implémentée, comme avant. La 2-relecteurs ne fait qu'hériter du même comportement documenté en section 7 (candidats insuffisants au dépôt ⇒ assignation partielle ou nulle, jamais retentée). Sacrifié parce que ça demande un déclencheur (event/job) hors du scope "endpoint synchrone" du reste de l'API, et le client n'en a pas fait une exigence explicite dans l'enveloppe.
- **Notification/rappel au relecteur qui n'a pas rendu sa note** : non implémenté. Le client demande juste que la note provisoire s'affiche, pas un système de relance.
- **Frontend (écran Relecteur/Étudiant) : affichage détaillé "provisoire vs finale"** : le backend expose le champ, mais l'habillage visuel (badge, couleur) est traité en dernière priorité si le temps le permet — sacrifié en premier si nécessaire, car sans impact sur la correction du backend qui est ce qui est noté à cette étape (10 points, tous côté process backend/migration/analyse).

Priorité : migration + backend (assignation, relecture, contrat) d'abord — c'est ce que l'enveloppe évalue. Frontend en dernier, sacrifiable.

## 8. Contraintes techniques

- **Backend (B1-B6)** : Java 17+, Maven (wrapper commité). Contrat `api/contrat.yaml` scrupuleusement respecté. Architecture en 3 couches (Controller/Service/Repository) et pattern DTO imposé. `@RestControllerAdvice` pour les erreurs. Base de données versionnée avec Flyway/Liquibase (migrations incluses). Tests unitaires métier et d'intégration HTTP.
- **Frontend (F1-F3)** : React déclaré dans le README. 3 écrans (Formateur, Étudiant, Relecteur). Appels API centralisés, états de chargement gérés, aucune logique métier redondante dans le client (ex: la moyenne vient du backend).
- **Démarrage** : via `docker compose up` ou un README clair, avec des données préchargées.

## 9. Livrables

- Le dépôt GitHub avec l'historique complet des commits (`[JALON] analyse`, `[JALON] v0.1`, `[JALON] v1.0`).
- Documentation (Cahier des charges, 4 diagrammes, contrats d'API, BACKLOG.md).
- Code source Frontend (React) et Backend (Spring Boot).
- Fichier de soumission (`SOUMISSION.md`) déposé sur la plateforme.

## 10. Démarche prévue

1. **Étape 1 :** Analyse détaillée, formalisation de ce document, réalisation des 4 diagrammes (Cas d'utilisation, Classes, Séquence, États). Mise à jour du contrat API. Création des issues (Backlog). Validation puis `[JALON] analyse`.
2. **Étape 2 :** Développement en parallèle du Frontend et du Backend (version initiale). Passage des tests et respect strict des contrats d'API pour l'intégration. Poussée du `[JALON] v0.1`.
3. **Étape 3 :** Demande de l'enveloppe au surveillant une fois `[JALON] v0.1` poussé, analyse des impacts, mise à jour de la documentation.
4. **Étape 4 :** Implémentation des changements de l'enveloppe et livraison du `[JALON] v1.0`.
5. **Étape 5 :** Remplissage du `README` final, `CHANGELOG.md`, vérification à froid depuis un clone vierge, et dépôt du document de soumission avant 18h00.

**Definition of Done — une issue est terminée quand :**
- Le code compile et passe les tests unitaires.
- Le contrat d'API est respecté.
- Les erreurs sont proprement interceptées.
- Le code source est poussé sur le dépôt distant.

---

## Journal des révisions

| Version | Quand | Ce qui a changé et pourquoi |
|---|---|---|
| 1 | 25/09 | Version initiale - Rédaction post-analyse |
| 1.1 | 25/09 | Le surveillant a publié une révision du sujet (5 étapes au lieu de 6, épreuve Git supprimée, enveloppe remise à la demande au lieu d'un script). §9 et §10 mis à jour en conséquence, aucun impact sur les EF/RG/diagrammes. |
| 1.2 | 25/09 | Enveloppe étape 3 : changement de besoin double relecteur. RG9 remplacée par RG12/RG13 (§6), analyse d'impact complète et sacrifice de périmètre en §7bis. Conséquence directe, traité dans un commit dédié sur `evolution/double-relecteur-rg6` (séparé du bugfix #37). |
