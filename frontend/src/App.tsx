import { useState } from 'react';
import EcranEtudiant from './components/Etudiant/EcranEtudiant';
import EcranFormateur from './components/Formateur/EcranFormateur';
import EcranRelecteur from './components/Relecteur/EcranRelecteur';

type Role = 'FORMATEUR' | 'ETUDIANT' | 'RELECTEUR' | null;

function App() {
  const [role, setRole] = useState<Role>(null);

  if (!role) {
    return (
      <div className="login-container">
        <div className="login-card">
          <h1 className="login-title">
            Bienvenue sur <span style={{ color: 'var(--primary)' }}>Présence & Relecture</span>
          </h1>
          <p className="login-subtitle">Sélectionnez votre profil pour continuer</p>
          
          <div className="role-buttons">
            <button className="role-btn formateur" onClick={() => setRole('FORMATEUR')}>
              <span className="role-icon">👨‍🏫</span>
              <span className="role-text">Je suis Formateur</span>
              <span className="role-desc">Créer des sessions et voir le tableau de bord</span>
            </button>
            
            <button className="role-btn etudiant" onClick={() => setRole('ETUDIANT')}>
              <span className="role-icon">🎓</span>
              <span className="role-text">Je suis Étudiant</span>
              <span className="role-desc">Marquer ma présence et déposer mon travail</span>
            </button>
            
            <button className="role-btn relecteur" onClick={() => setRole('RELECTEUR')}>
              <span className="role-icon">✍️</span>
              <span className="role-text">Je suis Relecteur</span>
              <span className="role-desc">Évaluer le travail d'un autre étudiant</span>
            </button>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="app-container">
      <header className="app-header">
        <h1 style={{ margin: 0, fontSize: '1.5rem' }}>
          Plateforme <span style={{ color: 'var(--primary)' }}>KFOKAM48</span>
        </h1>
        <button className="btn btn-outline" onClick={() => setRole(null)}>
          🚪 Déconnexion
        </button>
      </header>
      
      <main>
        {role === 'FORMATEUR' && <EcranFormateur />}
        {role === 'ETUDIANT' && <EcranEtudiant />}
        {role === 'RELECTEUR' && <EcranRelecteur />}
      </main>
    </div>
  );
}

export default App;
