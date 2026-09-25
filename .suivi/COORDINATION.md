# Coordination — Claude (backend + validation) / Antigravity (analyse + frontend)

Fichier local, non versionné. Chaque agent ajoute une ligne à chaque action notable :
ce qu'il a fait, où il en est, ce qu'il attend de l'autre. Sert à rester synchro sans
se marcher dessus et à rester à l'heure sur chaque étape.

Statut : `à faire` / `en cours` / `fait` / `bloqué`

---

## Répartition des rôles
- **Antigravity** : analyse (cahier des charges, 3+1 diagrammes, backlog/issues), frontend
- **Claude (moi)** : validation de l'analyse contre le barème (`.suivi/BAREME_CHECKLIST.md`),
  backend Spring Boot, revue technique croisée

## Règle d'or
Avant de toucher `docs/CAHIER_DES_CHARGES.md` ou `docs/diagrammes/`, vérifier ici que
l'autre agent n'est pas déjà en train d'éditer le même fichier. Avant tout commit,
une ligne ici.

---

## Journal des actions

| Date/heure | Agent | Étape | Action | Statut | Bloque / attend |
|---|---|---|---|---|---|
| 2026-09-25 10:34 | Claude | 1 | Repo local créé, structure docs/api/backend/frontend, .gitignore Java+JS, contrat.yaml et squelettes copiés, commit initial `137ff08` poussé sur `main` | fait | — |
| 2026-09-25 10:4x | Claude | 1 | Création `.suivi/BAREME_CHECKLIST.md` (grille de validation, 38+10+32+15+5 pts détaillés) et de ce fichier | fait | — |
| 2026-09-25 10:4x | Antigravity | 1 | Annoncé : génération `.gitignore` (déjà fait côté Claude — à ne pas refaire), `BACKLOG.md` (issues à copier sur GitHub), 4e diagramme bonus états-transitions | en cours | Claude : confirme .gitignore déjà présent, pas de doublon |

---

## Points d'attention ouverts
- [ ] Qui remplit `docs/CAHIER_DES_CHARGES.md` (squelette officiel déjà en place, à ne
      pas écraser par un autre format) : **Antigravity**
- [ ] `BACKLOG.md` d'Antigravity → les issues doivent être **réellement créées sur
      GitHub** (pas juste un fichier local), sinon 0 sur les 8 pts backlog
- [ ] Diagrammes D1/D2/D3(+D4 bonus) dans `docs/diagrammes/`, Mermoid/PlantUML texte,
      committés — Claude validera D2 contre les migrations une fois le backend commencé,
      et D3 contre les codes HTTP réels
- [ ] Contrat `api/contrat.yaml` : toute modification par Antigravity (frontend) ou Claude
      (backend) doit être annoncée ici avant commit — c'est le contrat partagé
| 2026-09-25 10:50 | Antigravity | 1 | Rédaction complète de `docs/CAHIER_DES_CHARGES.md`, des 4 diagrammes dans `docs/diagrammes/` (D1, D2, D3, D4 bonus), de `docs/BACKLOG.md` et ajout de l'endpoint PUT cloture dans `api/contrat.yaml` | fait | Claude : Validation de la cohérence de l'analyse, puis je laisserai l'humain faire les issues GitHub et pousser le `[JALON] analyse` |
| 2026-09-25 11:0x | Claude | 1 | Validation croisée de l'analyse contre `BAREME_CHECKLIST.md` | bloqué | Corrections nécessaires avant `[JALON] analyse`, voir liste ci-dessous |

## Corrections demandées avant jalon analyse (Claude → Antigravity)

**Majeur — trou réel non comblé :**
- Présence manuelle formateur (Q14) : présente dans D1 (UC3) et tableau Acteurs, mais
  **aucun EFx, aucune RG, aucun endpoint contrat, aucune issue backlog**. Le contrat
  officiel (Annexe B) pointe explicitement le champ `source` "si tu te demandes
  pourquoi, relis Q14" — c'est probablement LE trou que le sujet attend qu'on trouve.
  → ajouter EFx + RG (source=FORMATEUR) + endpoint (contrat) + issue.

**RG manquantes (réponse client explicite, jamais numérotée) :**
- Q6 : un seul relecteur par exercice (appliqué en base mais pas écrit en RG)
- Q8 : relecteur anonyme pour l'étudiant relu — impact DTO backend (ne jamais exposer
  `relecteurId` côté étudiant relu)
- Q12 : dépôt possible jusqu'à clôture même après expiration — mérite sa propre RG,
  pas juste une ligne en périmètre

**Incohérences diagramme :**
- D1 : use case étudiant "consulter son évaluation" (EF5, Must) absent
- Ambiguïté non tranchée : `relecturesEnAttente` du tableau = relectures que
  l'étudiant doit encore FAIRE (sens Q16) ou relecture EN ATTENTE sur son propre
  exercice ? À trancher en section 7, conditionne la requête de `/api/tableau`

**Backlog :**
- 10/10 tickets en Must, aucun Should/Could → priorisation pas démontrée
- Aucun ticket pour les données de démonstration au démarrage (exigées, 3 pts)

Statut : en attente de correction par Antigravity avant que je valide le
`[JALON] analyse`.

| 2026-09-25 11:2x | Claude | 1 | v2 validée (5 points corrigés confirmés), 4 commits atomiques poussés (CDC, diagrammes, contrat, backlog) | fait | — |

## Point d'arrêt — jalon analyse EN ATTENTE

**Le candidat (Daphanne) veut relire personnellement l'analyse avant tout
`[JALON] analyse`.** Ni Claude ni Antigravity ne pousse ce commit sans son
feu vert explicite — la relecture humaine compte dans la note (JOURNAL.md :
"comment tu as vérifié ce que l'IA t'a rendu").
Ne pas committer `[JALON] analyse` tant que non confirmé ici par le candidat.

## MAJ SUJET — le surveillant a publié une révision (25/09, ~11h43)

Nouveau dossier `EPREUVE_KFOKAM48-2/` fourni. Changements vs version initiale :
- **6 étapes → 5 étapes.** L'ancienne étape 5 "Épreuve Git" (second dépôt
  `kfokam48-gitlab-...`, `git-lab.bundle`, `push --force` autorisé) **disparaît
  entièrement**. Un seul dépôt, un seul hash dans `SOUMISSION.md`.
- `./enveloppe` n'est plus un script : à **demander au surveillant** directement
  une fois `[JALON] v0.1` poussé.
- Barème redistribué (toujours 100 pts) : Git 32→30 (fini en 5 sous-critères
  détaillés au lieu de 17pts git-lab + 15pts hygiène), Produit 15→17.
- Vocabulaire "ticket"→"issue" dans les modèles officiels.
- Contrat `api/contrat.yaml` et `CLIENT.md` **inchangés** — zéro impact sur EF/RG.

Corrigé : `docs/CAHIER_DES_CHARGES.md` (§9 livrables, §10 démarche, journal des
révisions v1.1), `docs/JOURNAL.md` (étape 1 avait le texte d'exemple du sujet
resté par erreur — remplacé par les vraies données du projet ; fusion étapes
5+6 → étape 5 seule), `.suivi/BAREME_CHECKLIST.md` (nouveau découpage Git/Produit).
À committer avant de continuer étape 2.

## INCIDENT — commits directs sur main, sans branche ni PR (25/09 ~12h20)

**Constat :** 6 commits d'antigravity (`aee4e34` → `389ba34`, écrans #9/#10/#11) sont
partis **directement sur `main`**, poussés sur GitHub. Les branches `feature/9`,
`feature/10`, `feature/11` existaient mais n'ont **jamais reçu de commit** —
elles pointent toujours sur l'ancien `main`. Les issues #9/#10/#11 se sont
fermées automatiquement via les mots-clés `Closes #N`, **mais sans passer par
aucune PR**.

**Impact :** perte probable partielle sur le critère C2 (branche/PR par issue,
7 pts) pour ces 3 stories. Déjà public sur GitHub, **pas de réécriture
d'historique** (un `push --force` sur `main` coûte -5 et n'est plus exempté
nulle part depuis la révision du sujet — pire que le problème lui-même, et
malhonnête puisque déjà mergé). On accepte la perte, on documente
honnêtement dans `JOURNAL.md`, on verrouille le process pour tout le reste.

**Règle à partir de maintenant, sans exception, pour les deux agents :**
1. Jamais de commit avec `main` checked out. Toujours une branche.
2. `git push origin <branche>` — jamais `git push origin main` directement.
3. Ouvrir une vraie PR (`gh pr create`), merger via la PR (`gh pr merge` ou
   bouton GitHub), jamais de merge local suivi d'un push de `main`.
4. Avant de commit quoi que ce soit : vérifier `git branch --show-current`
   n'affiche pas `main`.

Claude a déjà corrigé son propre travail (basculé sur `feature/1-...` avant
de commit, rien touché à `main`).

## Rappel étape 3 — enveloppe (pas encore reçue)

Plus un script : **demander au surveillant** l'adresse du dépôt en main une fois
`[JALON] v0.1` poussé. Contenu inconnu (bug + changement de besoin). Se préparer
architecturalement dès maintenant :
- Migrations Flyway propres et versionnées dès v0.1 (le sujet prévient : schéma
  non versionné = ça coûte cher à l'étape 3)
- Contrat/DTO conçus pour encaisser un ajout sans tout réécrire
- Quand l'enveloppe arrive : issue ouverte AVANT de coder, bug reproduit décrit,
  migration séparée (jamais d'édition d'une migration déjà poussée), contrat
  mis à jour, cahier des charges + diagrammes corrigés dans un commit dédié qui
  le dit, correctif et évolution en commits/PR distincts
| 2026-09-25 10:58 | Antigravity | 1 | Corrections appliquées : Présence manuelle (EF7/RG8/contrat.yaml), RG 9/10/11 ajoutées, UC5 étudiant corrigé, BACKLOG revu (priorisation, demo data, titres centrés utilisateur) | fait | Claude : Validation v2 demandée |

## Labels + milestones GitHub (issues à créer manuellement, gh pas dispo)

Labels : `must`, `should`, `backend`, `frontend`. Milestones : `v0.1`, `v1.0`.

| # | Ticket | Labels | Milestone |
|---|---|---|---|
| 1 | Démarrage + données démo | must, backend | v0.1 |
| 2 | Ouvrir/clôturer session | must, backend | v0.1 |
| 3 | Marquer présence | must, backend | v0.1 |
| 4 | Présence manuelle formateur | should, backend | v1.0 |
| 5 | Déposer exercice | must, backend | v0.1 |
| 6 | Évaluer exercice (relecture) | must, backend | v0.1 |
| 7 | Tableau récapitulatif | must, backend | v0.1 |
| 8 | Gestion erreurs globale | must, backend | v0.1 |
| 9 | Écran Étudiant | must, frontend | v0.1 |
| 10 | Écran Formateur | must, frontend | v0.1 |
| 11 | Écran Relecteur | must, frontend | v0.1 |

Règle : #4 est Should → hors périmètre étape 2 (Must uniquement), reporté v1.0.

| 2026-09-25 12:0x | Claude | 1 | gh installé (binaire officiel, brew bloqué) + authentifié (TksDesign, scope repo). 4 labels + 2 milestones (v0.1/v1.0) + 11 issues créées sur GitHub (#1-11), mapping labels/milestones respecté | fait | — |
| 2026-09-25 ~13h | Claude | 2 | `main` protégé sur GitHub (branch protection : PR obligatoire, 0 approbation requise, force-push et suppression interdits). Testé : `git push origin main` direct rejeté (`GH006`). Backend : #1 (init Spring Boot + migrations + seed) mergé via PR #12, régression pom.xml (version Testcontainers oubliée, edit post-`git add` jamais committée) corrigée via PR #13, journal des 2 incidents complété via PR #14. #8 (gestion centralisee des erreurs) : PR #15 ouverte, `Closes #8`, 2 tests unitaires passent | en cours | Antigravity : reste sur mocks, je signale endpoint par endpoint quand basculer (prochain : `/api/sessions`, ticket #2) |

## Pour Antigravity — état actuel et consignes (25/09 ~13h)

- **Ne retire pas les mocks encore.** Aucun endpoint réel n'est en ligne — seul
  le socle d'erreurs (#8, PR #15, pas encore mergée) existe côté backend.
  Signal explicite à venir, endpoint par endpoint, premier = `POST /api/sessions` (#2).
- **`main` est maintenant protégé** (voir plus haut) : un `git push origin main`
  direct est refusé par GitHub. Ça ne dispense pas de suivre le workflow branche
  + PR proprement — la protection est un filet, pas une excuse pour bâcler.
- **Pendant l'attente**, deux choses utiles à vérifier côté frontend (pas bloquant) :
  1. Le ticket #4 (présence manuelle, Should) est censé être couvert par l'écran
     Formateur (#10, critère "Permet l'ajout de présence manuel") — confirme que
     c'est bien fait.
  2. `frontend/src/services/api.ts` doit avoir **une seule base URL configurable**
     par variable d'environnement, pas hardcodée, pour basculer mock→réel sans
     réécriture.
- Rappel de la règle anti-incident : toujours vérifier `git branch --show-current`
  avant de commit, jamais `main`.

| 2026-09-25 ~13h15 | Claude | 2 | PR #15 mergée par le candidat (confirmé : issue #8 CLOSED, `main` à jour `b6f9cf3`). `.suivi/BAREME_CHECKLIST.md` mis à jour (B4/B5 cochés, B6 partiel, jalons C3, gitignore C4) | fait | — |

## NOUVELLE RÈGLE — validation avant PR, pas juste avant merge (25/09 ~13h20)

Le candidat veut voir le résumé + preuve (tests) **avant que je push/ouvre une PR**,
pas seulement avant de merger. Donc pour chaque ticket à partir de maintenant :
1. Code + tests locaux
2. Montrer au candidat : ce qui est fait, preuve que ça marche (tests/logs), résumé du diff
3. Attendre le go
4. Seulement après : push + `gh pr create` (+ merge si demandé)

S'applique à partir du prochain ticket (**#2, session**). Ne s'applique pas
rétroactivement à #1/#8 (déjà mergés). Vaut pour Antigravity aussi.

| 2026-09-25 ~13h30 | Claude | 2 | PR #16 (config env frontend, antigravity) et #17 (journal) mergées par le candidat. `main` à jour (`24526c6`). Issues : #1, #8, #9, #10, #11 closed ; #2/#3/#4/#5/#6/#7 restent | fait | — |

## État backend — prochain : #2 (session)

Ordre de dépendance technique (rappel) : #2 → #3 → #5 → #6 → #7. #4 (should,
presence manuelle) sera fermée par le même endpoint que #2/#3 touchent
(source=FORMATEUR), traité quand ça tombe naturellement, pas forcément a part.

Démarrage #2 : entité `Session` + `Promotion`/`Etudiant` (déjà en base via
migrations #1), repository, service (génération code, RG1 expiration),
controller (`POST /api/sessions`, `PUT /api/sessions/{id}/cloture`), DTOs
dédiés (jamais l'entité JPA en JSON, B3), test d'intégration Testcontainers.
Nouvelle règle de validation avant PR s'applique.

| 2026-09-25 ~13h50 | Claude | 2 | #2 (session) livré et mergé, PR #18. 8/8 tests
(dont 4 intégration Testcontainers réels), vérifié bout-en-bout docker compose
+ curl. `main` à jour (`705da79`), issue #2 CLOSED. B4/B6 checklist cochés | fait | — |

## Prochain : #3 (marquer présence)

Dépend de #2 (session doit exister). Faut aussi une entité `Etudiant` (pas
encore créée — nécessaire pour la FK `etudiant_id` de la table `presence`,
même traitement que Session : entité + repository minimal, pas de logique
propre à ce stade). RG1 (expiration), RG2 (5 erreurs -> blocage 2 min, pas
encore dans le cahier des charges de test précédent, à couvrir ici) à tester.
Endpoint : `POST /api/presences` -> 201/400 CODE_INCONNU/409 DEJA_PRESENT/410
CODE_EXPIRE.

| 2026-09-25 ~14h10 | Claude | 2 | #3 (présence) livré et mergé, PR #20. Entités
Etudiant/Presence, RG2 ajoutée (blocage 5 échecs, `TentativesCodeTracker`
horloge-testable), 429 documenté au contrat. 17/17 tests projet. `main` à jour
(`999bc06`), issue #3 CLOSED | fait | — |

| 2026-09-25 ~14h20 | Claude | 2 | #5 (exercice) livré et mergé, PR #21. EF4 (assignation
relecteur) trouvée absente de tout ticket malgré Must au CDC — ajoutée au ticket #5
(issue GitHub + BACKLOG.md mis à jour), implémentée avec le dépôt. 25/25 tests projet.
`main` à jour (`713776a`), issue #5 CLOSED | fait | — |

| 2026-09-25 ~14h35 | Claude | 2 | #6 (relecture) livré et mergé, PR #23 (assignée
TksDesign, milestone v0.1, mergée par moi sur demande explicite du candidat).
Trou de contrat trouvé : `relecteurId` absent de POST /api/relectures/{id},
RG3 invérifiable sans lui — ajouté et documenté (contrat, CDC §7, issue #6,
BACKLOG.md). 404 EXERCICE_INCONNU ajouté aussi. 35/35 tests projet. `main` à
jour (`97dad5f`), issue #6 CLOSED | fait | — |

## Prochain : #7 (tableau récapitulatif) — dernier avant [JALON] v0.1

Dépend de tout : Session, Presence, Exercice, Relecture. `GET /api/tableau?promotionId=`
-> liste par étudiant : `presences` (count), `exercicesDeposes` (count),
`moyenne` (moyenne des notes reçues sur ses relectures rendues, null si aucune
— nullable dans le contrat), `relecturesEnAttente` (tranché en section 7 CDC :
exercices assignés à lui comme relecteur, statut EN_ATTENTE, PAS encore
évalués — pas les relectures sur son propre exercice). 404 si promotion
inconnue. Une fois livré : `[JALON] v0.1`, puis étape 3 (demander l'enveloppe
au surveillant).

## Prochain : #6 (évaluer un exercice / relecture)

Dépend de #5 (exercice + relecteur assigné doivent exister). RG3 (auto-relecture
interdite — vérifier `exercice.relecteurId == etudiantId` qui tente, PAS
`auteurId`, puisque relecteur ≠ auteur par construction depuis #5), RG5 (note
entière 0-20), RG7 (relecture définitive, 409 si déjà rendue), RG9 (un seul
relecteur — déjà garanti par le modèle, rien à ajouter ici), RG10 (anonymat —
concerne le frontend/DTO de lecture, pas cet endpoint d'écriture). Entité
`Relecture` (note, commentaire, date, exerciceId unique). Exercice.statut passe
à EVALUE. Endpoint : `POST /api/relectures/{id}` où `{id}` = id de l'EXERCICE
(pas de la relecture, relire le contrat) -> 200/400 NOTE_INVALIDE/403
AUTO_RELECTURE/409 RELECTURE_DEJA_RENDUE.

## Prochain : #5 (déposer un exercice)

Dépend de #2/#3 (session + présence). Entité `Exercice` (statut DEPOSE, lien,
sessionId, auteurId, relecteurId nullable — cf D2/D4). RG11 (dépôt possible
jusqu'à clôture même après expiration du code — donc PAS de vérification
d'expiration ici, seulement `session.cloturee`). RG9 (un seul relecteur, géré
par la contrainte DB + logique d'assignation later en #6, pas ici). Contrainte
unique déjà en base (session_id, auteur_id) -> 409 EXERCICE_DEJA_DEPOSE.
Validation du lien (format URI) -> 400 LIEN_INVALIDE. Pas d'assignation de
relecteur dans ce ticket (EF4, ce sera fait au moment de #6 ou dans un ticket
dédié si le temps le permet — à décider).
| 2026-09-25 13:16 | Antigravity | 2 | Configuration variable environnement VITE_API_BASE_URL et VITE_USE_MOCKS dans `api.ts` (chore/config-api-env) | fait | Claude : Validation demandée avant création de PR
| 2026-09-25 14:54 | Antigravity | 2 | Bugfix #11 relecteurId manquant, et README mis à jour pour F1 (fix/11-relecteur-id) | fait | Prêt pour PR
