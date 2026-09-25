import { useState } from 'react';
import EcranEtudiant from './components/Etudiant/EcranEtudiant';
import EcranFormateur from './components/Formateur/EcranFormateur';
import EcranRelecteur from './components/Relecteur/EcranRelecteur';

function App() {
  const [role, setRole] = useState<'FORMATEUR' | 'ETUDIANT' | 'RELECTEUR'>('ETUDIANT');

  return (
    <div className="app-container">
      <h1 style={{ textAlign: 'center', marginBottom: '2rem', fontSize: '2rem' }}>
        Épreuve Finale <span style={{ color: 'var(--primary)' }}>KFOKAM48</span>
      </h1>
      
      <div className="tabs-container">
        <button 
          className={`tab-button ${role === 'FORMATEUR' ? 'active' : ''}`}
          onClick={() => setRole('FORMATEUR')} 
        >
          👨‍🏫 Vue Formateur
        </button>
        <button 
          className={`tab-button ${role === 'ETUDIANT' ? 'active' : ''}`}
          onClick={() => setRole('ETUDIANT')} 
        >
          🎓 Vue Étudiant
        </button>
        <button 
          className={`tab-button ${role === 'RELECTEUR' ? 'active' : ''}`}
          onClick={() => setRole('RELECTEUR')} 
        >
          ✍️ Vue Relecteur
        </button>
      </div>

      <main>
        {role === 'FORMATEUR' && <EcranFormateur />}
        {role === 'ETUDIANT' && <EcranEtudiant />}
        {role === 'RELECTEUR' && <EcranRelecteur />}
      </main>
    </div>
  );
}

export default App;
