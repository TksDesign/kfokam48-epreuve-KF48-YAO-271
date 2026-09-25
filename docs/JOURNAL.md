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

**Fait :** Initialisation du Front-end avec React/Vite (TS). Création de la couche de services (`api.ts`) et de l'intercepteur de mock. Implémentation des 3 écrans (Étudiant, Relecteur, Formateur) sur leurs propres branches (`feature/9`, `11`, `10`) avec gestion stricte des erreurs, du chargement et intégration dans `App.tsx`.
**Bloqué :** `npm create vite` plantait à cause de la version de Node imposée par l'environnement (18.x) qui ne supportait pas la version la plus récente de `create-vite`. Contourné en forçant Vite v5.
**IA :** Antigravity a généré les 3 écrans en respectant les contraintes (couche API dédiée, F3, etc.). La conformité a été vérifiée en s'assurant que l'écran étudiant n'expose jamais le nom du relecteur (RG10) et que le formateur ne calcule pas les moyennes localement.

---

## Étape 3 — Enveloppe

**Fait :**

**Bloqué :**

**IA :**

**Ce que j'ai sorti du périmètre pour absorber le changement, et pourquoi :**

---

## Étape 4 — Version finale

**Fait :**

**Bloqué :**

**IA :**

---

## Étape 5 — Soumission

**Fait :**

**Ce que je referais autrement avec une journée de plus :**
