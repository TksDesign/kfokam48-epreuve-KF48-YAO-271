# Épreuve Finale KFOKAM48 — Frontend

**Framework choisi :** React (avec Vite), car il permet de développer rapidement des interfaces déclaratives avec une gestion d'état simple pour nos trois écrans, tout en offrant un écosystème très robuste.

## Démarrage rapide

Assurez-vous d'avoir Node.js (version 18+) installé.

1. **Installer les dépendances :**
   ```bash
   npm install
   ```

2. **Lancer l'application en mode développement :**
   ```bash
   npm run dev
   ```

3. **Générer le build de production (validation) :**
   ```bash
   npm run build
   ```

*L'application sera accessible sur `http://localhost:5173`.*

## Configuration API
Par défaut, le frontend utilise des **Mocks**.
Pour se connecter au vrai backend Spring Boot (qui tourne sur `http://localhost:8080`), modifiez le fichier `.env.local` (ou vos variables d'environnement) avec :

```env
VITE_USE_MOCKS=false
VITE_API_BASE_URL=http://localhost:8080/api
```

## Données de démonstration
Une fois branché au vrai backend, une promotion par défaut (Promo 2026) et des étudiants (Alice, Bob, Charlie) sont injectés automatiquement via le système de migration du backend pour permettre de tester immédiatement les fonctionnalités sans base vide.
