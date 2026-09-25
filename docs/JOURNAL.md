# Journal de bord — <matricule>

> Une entrée **par étape**, écrite **au moment où tu la termines**, pas à la fin de la journée.
> Trois lignes suffisent. Un journal rédigé d'un bloc juste avant de soumettre se repère
> immédiatement dans l'historique Git et ne compte pas.

Chaque entrée répond aux trois mêmes questions :

- **Fait** — ce que tu viens de terminer
- **Bloqué** — ce qui t'a coûté du temps, et combien
- **IA** — ce que tu lui as demandé, et **comment tu as vérifié sa réponse**

---

## Étape 1 — Analyse et conception

**Fait :** cahier des charges (7 exigences fonctionnelles, 11 règles de gestion), 4 diagrammes Mermaid (D1-D3 + D4 bonus états-transitions), 11 issues créées sur GitHub avec labels (must/should, backend/frontend) et milestones (v0.1/v1.0), contrat d'API complété (+2 endpoints : clôture session, présence manuelle), commit `[JALON] analyse` poussé.

**Bloqué :** contradiction entre Q10 et Q15 sur la modification d'une note après envoi. Tranchée en faveur de **Q15** (note définitive) : le contrat d'API imposé ne propose pas de `PUT/PATCH` sur `/api/relectures/{id}` et renvoie `409 RELECTURE_DEJA_RENDUE`, ce qui corrobore Q15 techniquement. Noté en section 7. Autre point bloquant : présence manuelle du formateur (Q14) mentionnée nulle part dans l'API imposée ni dans les diagrammes initiaux — trou comblé en ajoutant EF7/RG8 et un endpoint dédié.

**IA :** Antigravity a rédigé le premier jet (cahier des charges, diagrammes, backlog). Claude l'a validé contre le barème et relevé 5 manques (RG manquantes pour Q6/Q8/Q12, endpoint présence manuelle absent, backlog 100% Must sans priorisation). Corrections appliquées par Antigravity, relecture complète faite moi-même section par section contre les 16 réponses de `CLIENT.md` avant validation finale.

---

## Étape 2 — Première version

**Fait :** Initialisation du Front-end avec React/Vite (TS). Création de la couche de services (`api.ts`) et de l'intercepteur de mock, désormais pilotée par variables d'environnement (`VITE_API_BASE_URL`, `VITE_USE_MOCKS`, PR #16). Implémentation des 3 écrans (Étudiant, Relecteur, Formateur) avec gestion stricte des erreurs, du chargement et intégration dans `App.tsx`. Côté backend : `spring init` (Java 17, Spring Boot 4.1.1), schéma Flyway V1 calqué sur D2, V2 données démo, `docker-compose.yml`, PR #12 (Closes #1) mergée ; gestion centralisée des erreurs (`@RestControllerAdvice`, 11 exceptions métier couvrant tous les codes du contrat, 2 tests unitaires), PR #15 (Closes #8) mergée ; session formateur (`POST /api/sessions`, `PUT /api/sessions/{id}/cloture`, RG1 génération/expiration du code), PR #18 (Closes #2) mergée ; marquage de présence (`POST /api/presences`, RG1 réutilisée, RG2 blocage après 5 codes inconnus via un tracker à horloge injectée, nouveau code d'erreur 429 documenté au contrat), PR #20 (Closes #3) mergée ; dépôt d'exercice (`POST /api/exercices`, EF4/RG4 assignation aléatoire du relecteur parmi les présents — absente de tout ticket malgré Must au cahier des charges, ajoutée et tracée sur l'issue #5), PR #21 (Closes #5) mergée ; relecture (`POST /api/relectures/{id}`, RG3/RG5/RG7 — deuxième trou de contrat trouvé : `relecteurId` absent du corps imposé, RG3 invérifiable sans identité sans authentification (Q1), ajouté et documenté), PR #23 (Closes #6) mergée ; tableau récapitulatif (`GET /api/tableau`), PR #25 (Closes #7) mergée — tous les endpoints Must du contrat livrés ; troisième trou trouvé en auditant le frontend (EF5 sans endpoint pour qu'un étudiant consulte sa propre note), `GET /api/exercices?etudiantId=` ajouté, issue #26, PR #28 mergée ; polish UI + écran d'accueil de sélection du rôle par antigravity (issues #29/#30 créées a posteriori — les commits d'origine référençaient par erreur des numéros de PR), PR #31 mergée. **Test réel demandé par le candidat avant le jalon** (navigateur, vrai backend, pas mocks) : bug bloquant trouvé — aucune configuration CORS, toute requête réelle échouait — corrigé (issue #32, PR #33 mergée) et revérifié en conditions réelles. Deux points restants trouvés au même test : écran Relecteur inutilisable (liste d'exercices figée) et écran Étudiant n'affichant jamais la vraie note reçue — corrigés ensemble (liste réelle des exercices à évaluer + affichage note/commentaire réels), PR #34 en cours de revue. 46/46 tests au total, dont 21 d'intégration Testcontainers réels. Protection de branche activée sur `main` (PR obligatoire, force-push interdit) pour empêcher techniquement la récidive des incidents ci-dessous.

**Bloqué :** `npm create vite` plantait à cause de la version de Node imposée par l'environnement (18.x) qui ne supportait pas la version la plus récente de `create-vite`. Contourné en forçant Vite v5. **Incident git n°1 :** les 6 commits front de cette étape (dont ceux fermant #9/#10/#11) sont partis directement sur `main`, sans passer par les branches `feature/9`/`10`/`11` pourtant créées, ni par une PR. Repéré en vérifiant `git log --oneline --all` a posteriori. Pas de réécriture d'historique (déjà public, un `push --force` coûterait plus cher que l'erreur elle-même) : accepté, documenté, règle "jamais de commit avec main checked out" imposée pour la suite (`.suivi/COORDINATION.md`). **Incident n°2 :** premier build Docker du backend en échec (`pom.xml` : version Testcontainers manquante) ; corrigé, mais le correctif a été édité après le `git add` et n'a jamais été réellement committé — la PR #12 mergée contenait donc le `pom.xml` cassé, `main` a été temporairement non-sain. Repéré en tentant de repasser sur `main` (`git checkout` refusé à cause de modifications locales non commitées). Corrigé via PR #13 dédiée, poussée immédiatement. **Incident n°3 :** en voulant committer une simple mise à jour du journal, un `checkout -b` a échoué silencieusement (branche déjà existante) et le commit est parti sur `main` local — repéré immédiatement, jamais poussé, récupéré via le hash du commit (`git cherry-pick`) sur la bonne branche. Aucune conséquence publique, mais a motivé la mise en place de la protection de branche GitHub ci-dessus plutôt que de compter sur la seule discipline manuelle. **Incident n°4 :** `.suivi/COORDINATION.md` (outil de travail interne, dans `.gitignore`) a été force-ajouté par erreur dans un commit d'antigravity (`fc89f63`) — a provoqué sa disparition/réapparition du disque à plusieurs reprises en changeant de branche. Retiré du suivi git avant merge (commit dédié), sans toucher à `main`.

**IA :** Antigravity a généré les 3 écrans en respectant les contraintes (couche API dédiée, F3, etc.). La conformité a été vérifiée en s'assurant que l'écran étudiant n'expose jamais le nom du relecteur (RG10) et que le formateur ne calcule pas les moyennes localement. Claude a généré le squelette backend ; vérifié en faisant réellement tourner `docker compose up --build` et en interrogeant la base (`psql`) pour confirmer les 5 étudiants de démo, pas juste en lisant le code.

---

## Étape 3 — Enveloppe

**Fait :** Enveloppe récupérée après `[JALON] v0.1`, deux sujets traités en parallèle sur deux branches séparées. **Bug** (course concurrente sur `POST /api/presences`) : traduit le signalement client en scénario technique (check-then-act non atomique sur `existsBySessionIdAndEtudiantId` + `save`), issue #37 ouverte avant tout code, test qui échoue committé en premier (`[201, 500]` prouvé), fix dans un commit séparé référençant l'issue (`DataIntegrityViolationException` → 409 `DEJA_PRESENT`), PR #41 mergée. **Changement de besoin** (double relecteur, RG9→RG12/RG13) : analyse mise à jour en premier (CDC §6/§7bis, D2, D4) dans un commit dédié sans code, contrat d'API mis à jour, migration `V3` ajoutée (V1/V2 intactes, backfill des données existantes), travail découpé en 3 issues (#38 migration, #39 assignation, #40 rendu+moyenne+provisoire), PR #42 mergée séparément de la PR bug. 54/54 tests après les deux merges, migration testée en conditions réelles (Testcontainers).

**Bloqué :** en testant le rôle relecteur, découverte d'un deuxième bug (pas dans l'enveloppe) : la "mise en file d'attente" documentée en CDC §7 (exercice sans candidat présent) n'a en réalité jamais été implémentée — un exercice sans relecteur au dépôt reste orphelin indéfiniment. Décision : pas de fix isolé, absorbé dans le refactor RG12 (la logique d'assignation était de toute façon réécrite), documenté en CDC §7bis plutôt que caché.

**IA :** Claude a traduit les deux signalements client (verbatim, non techniques) en scénarios reproductibles, en s'appuyant sur la lecture directe du code (`PresenceService`, `ExerciceService`) plutôt que sur des suppositions. Vérifié par exécution réelle : le test de course a d'abord démontré le bug (`[201, 500]`), la migration `V3` a été appliquée sur une vraie base Postgres via Testcontainers (pas juste relue), et l'ensemble de la suite (54 tests) a tourné après chaque merge pour confirmer `main` sain.

**Ce que j'ai sorti du périmètre pour absorber le changement, et pourquoi :** réassignation différée d'un relecteur (quand un candidat manquant marque présence après coup) — reste non implémentée, comme avant le changement ; demande un déclencheur asynchrone hors du scope synchrone du reste de l'API, et le client ne l'a pas demandé explicitement. Notifications/rappel au relecteur qui n'a pas rendu sa note — non demandé, le client veut juste voir la note provisoire. Habillage visuel frontend (badge "provisoire" vs "finale") — sacrifié en premier si le temps manque, sans impact sur la correction backend qui est ce qui est noté à cette étape.

---

## Étape 4 — Version finale

**Fait :**

**Bloqué :**

**IA :**

---

## Étape 5 — Soumission

**Fait :**

**Ce que je referais autrement avec une journée de plus :**
