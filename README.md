# Présence & Relecture — KFOKAM48

Application de suivi de sessions de cours en direct : ouverture de session par
le formateur, marquage de présence par code, dépôt d'exercice, relecture par
les pairs (2 relecteurs distincts, note moyenne) et tableau de bord temps réel.

**Frontend :** React (Vite + TypeScript) — développement rapide, déclaratif, écosystème riche pour les 3 écrans requis.
**Backend :** Java 17 / Spring Boot 4.1.1, architecture en couches (Controller/Service/Repository/DTO).
**Base de données :** PostgreSQL, migrations versionnées Flyway.

## Démarrage (3 commandes max)

Prérequis : Docker et Docker Compose.

```bash
git clone https://github.com/TksDesign/kfokam48-epreuve-KF48-YAO-271.git
cd kfokam48-epreuve-KF48-YAO-271
docker compose up --build
```

- Frontend : http://localhost:3000
- Backend (API) : http://localhost:8080/api
- Des données de démonstration sont chargées automatiquement (1 promotion, 5 étudiants — migration `V2`).

## Structure du dépôt

```
backend/           Spring Boot (Java 17, Maven wrapper commité)
frontend/          React + Vite + TypeScript
api/contrat.yaml   Contrat API (OpenAPI)
docs/              Cahier des charges, diagrammes (Mermaid), journal de bord
```

## Tests

```bash
cd backend && ./mvnw test
```

Tests unitaires + tests d'intégration sur Postgres éphémère (Testcontainers) — aucune base locale requise.

## Documentation

- [Cahier des charges](docs/CAHIER_DES_CHARGES.md)
- [Diagrammes](docs/diagrammes/)
- [Journal de bord](docs/JOURNAL.md)
- [Contrat API](api/contrat.yaml)
- [CHANGELOG](CHANGELOG.md)
