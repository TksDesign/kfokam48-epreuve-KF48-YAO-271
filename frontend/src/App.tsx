import { useState } from 'react';
import EcranEtudiant from './components/Etudiant/EcranEtudiant';
import EcranFormateur from './components/Formateur/EcranFormateur';
import EcranRelecteur from './components/Relecteur/EcranRelecteur';

function App() {
  const [role, setRole] = useState<'FORMATEUR' | 'ETUDIANT' | 'RELECTEUR'>('ETUDIANT');

  return (
    <div style={{ fontFamily: 'sans-serif', margin: '20px' }}>
      <h1>Épreuve Finale KFOKAM48</h1>
      
      <div style={{ marginBottom: '20px', borderBottom: '1px solid #ccc', paddingBottom: '10px' }}>
        <button 
          onClick={() => setRole('FORMATEUR')} 
          style={{ fontWeight: role === 'FORMATEUR' ? 'bold' : 'normal', marginRight: '10px' }}
        >
          Vue Formateur
        </button>
        <button 
          onClick={() => setRole('ETUDIANT')} 
          style={{ fontWeight: role === 'ETUDIANT' ? 'bold' : 'normal', marginRight: '10px' }}
        >
          Vue Étudiant
        </button>
        <button 
          onClick={() => setRole('RELECTEUR')} 
          style={{ fontWeight: role === 'RELECTEUR' ? 'bold' : 'normal' }}
        >
          Vue Relecteur
        </button>
      </div>

      <div>
        {role === 'FORMATEUR' && <EcranFormateur />}
        {role === 'ETUDIANT' && <EcranEtudiant />}
        {role === 'RELECTEUR' && <EcranRelecteur />}
      </div>
    </div>
  );
}

export default App;
