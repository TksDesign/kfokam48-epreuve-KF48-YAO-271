# Soumission — Épreuve finale fullstack KFOKAM48

> Remplis ce fichier, **vérifie tes deux liens depuis une fenêtre de navigation privée**,
> puis téléverse-le sur la plateforme **avant 18h00**.
> Sans ce dépôt sur la plateforme, tu n'as rien rendu.

---

## Candidat

| | |
|---|---|
| Nom et prénom(s) | Tissie Kuate Shanonn Daphane |
| Matricule | KF48-YAO-271 |
| Centre | Yaoundé |
| Compte GitHub | TksDesign |

## Projet

| | |
|---|---|
| Dépôt (public) | `https://github.com/TksDesign/kfokam48-epreuve-KF48-YAO-271` |
| Commit final — hash complet, 40 caractères | *(à remplir après le tout dernier push)* |
| Branche | `main` |

## Technique

| | |
|---|---|
| Frontend utilisé | React (Vite + TypeScript) |
| Base de données | PostgreSQL (migrations Flyway) |
| Commandes de démarrage | `git clone`, `cd kfokam48-epreuve-KF48-YAO-271`, `docker compose up --build` |

## Ce que j'ai livré

Les 5 opérations imposées du contrat + les endpoints ajoutés et justifiés (clôture session, présence manuelle, consultation de sa propre note) sont fonctionnels et testés (58 tests, dont intégration Testcontainers). Le changement de l'enveloppe étape 3 (double relecteur, RG12/RG13) est implémenté de bout en bout : migration, backend, contrat. Ce qui ne fonctionne pas ou a été volontairement laissé de côté : la ré-assignation différée d'un relecteur manquant (candidat insuffisant au dépôt) n'est jamais retentée automatiquement, et l'habillage visuel frontend "note provisoire vs finale" n'a pas de style dédié — les deux sont documentés et justifiés dans `docs/CAHIER_DES_CHARGES.md` §7bis.

---

## Avant de téléverser, vérifie

- [x] Mon dépôt est **public** et s'ouvre en navigation privée
- [ ] Mon hash fait bien **40 caractères** et existe sur GitHub
- [ ] Tout mon travail est **poussé** — `git status` est propre
- [x] Mon `README` a été testé depuis un clone vierge, dans un dossier vide
- [x] Mon `JOURNAL.md` et mon cahier des charges sont dans `docs/`
- [x] Les trois commits `[JALON]` sont poussés et dans le bon ordre

---

**Déclaration.** J'ai réalisé ce travail seul. Les outils d'IA étaient autorisés sans restriction et je les ai utilisés ; mon journal indique où et comment j'ai vérifié leurs réponses. Mon dépôt restera public et inchangé jusqu'à la publication des résultats.

Signature : ______________________  Date : __________
