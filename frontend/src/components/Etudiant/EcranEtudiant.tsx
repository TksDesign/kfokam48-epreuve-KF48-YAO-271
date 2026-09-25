import { useState } from 'react';
import { api, ApiError } from '../../services/api';

export default function EcranEtudiant() {
  const [etudiantId, setEtudiantId] = useState<number | ''>('');
  const [code, setCode] = useState('');
  const [lien, setLien] = useState('');
  
  const [loadingPresence, setLoadingPresence] = useState(false);
  const [errorPresence, setErrorPresence] = useState<string | null>(null);
  const [successPresence, setSuccessPresence] = useState(false);
  const [sessionId, setSessionId] = useState<number | null>(null);

  const [loadingExercice, setLoadingExercice] = useState(false);
  const [errorExercice, setErrorExercice] = useState<string | null>(null);
  const [successExercice, setSuccessExercice] = useState(false);

  // Mock list for Q1
  const etudiantsMocks = [
    { id: 1, nom: 'Alice Martin' },
    { id: 2, nom: 'Bob Dupuis' },
    { id: 3, nom: 'Charlie Legrand' },
  ];

  const handlePresence = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!etudiantId || !code) return;

    setLoadingPresence(true);
    setErrorPresence(null);
    setSuccessPresence(false);

    try {
      const res = await api.marquerPresence(code, Number(etudiantId));
      setSuccessPresence(true);
      setSessionId(res.sessionId); // Garde la session en cours pour le dépôt
    } catch (err: any) {
      setErrorPresence(err.message || 'Erreur inconnue');
    } finally {
      setLoadingPresence(false);
    }
  };

  const handleDepot = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!etudiantId || !sessionId || !lien) return;

    setLoadingExercice(true);
    setErrorExercice(null);
    setSuccessExercice(false);

    try {
      await api.deposerExercice(sessionId, Number(etudiantId), lien);
      setSuccessExercice(true);
    } catch (err: any) {
      setErrorExercice(err.message || 'Erreur lors du dépôt');
    } finally {
      setLoadingExercice(false);
    }
  };

  return (
    <div style={{ maxWidth: 400, margin: '0 auto', padding: '1rem' }}>
      <h2>Espace Étudiant</h2>

      {/* SECTION PRÉSENCE */}
      <section style={{ border: '1px solid #ccc', padding: '1rem', marginBottom: '1rem' }}>
        <h3>1. Marquer ma présence</h3>
        <form onSubmit={handlePresence}>
          <div style={{ marginBottom: '1rem' }}>
            <label>Mon nom : </label>
            <select 
              value={etudiantId} 
              onChange={e => setEtudiantId(e.target.value ? Number(e.target.value) : '')}
              disabled={successPresence}
              required
            >
              <option value="">-- Choisir --</option>
              {etudiantsMocks.map(e => (
                <option key={e.id} value={e.id}>{e.nom}</option>
              ))}
            </select>
          </div>

          <div style={{ marginBottom: '1rem' }}>
            <label>Code session : </label>
            <input 
              type="text" 
              value={code} 
              onChange={e => setCode(e.target.value)}
              disabled={successPresence}
              required
            />
          </div>

          {!successPresence && (
            <button type="submit" disabled={loadingPresence}>
              {loadingPresence ? 'Validation...' : 'Valider Présence'}
            </button>
          )}

          {errorPresence && <p style={{ color: 'red' }}>{errorPresence}</p>}
          {successPresence && <p style={{ color: 'green' }}>✓ Présence validée !</p>}
        </form>
      </section>

      {/* SECTION DÉPÔT (visible seulement si présent) */}
      {successPresence && (
        <section style={{ border: '1px solid #ccc', padding: '1rem' }}>
          <h3>2. Déposer mon exercice</h3>
          <form onSubmit={handleDepot}>
            <div style={{ marginBottom: '1rem' }}>
              <label>Lien de l'exercice (URL) : </label>
              <input 
                type="url" 
                value={lien} 
                onChange={e => setLien(e.target.value)}
                required
              />
            </div>

            <button type="submit" disabled={loadingExercice}>
              {loadingExercice ? 'Dépôt...' : 'Déposer'}
            </button>

            {errorExercice && <p style={{ color: 'red' }}>{errorExercice}</p>}
            {successExercice && <p style={{ color: 'green' }}>✓ Exercice déposé !</p>}
          </form>
        </section>
      )}

      {/* SECTION EVALUATION */}
      {successExercice && (
        <section style={{ border: '1px solid #ccc', padding: '1rem', marginTop: '1rem', backgroundColor: '#f9f9f9' }}>
          <h3>3. Mon Évaluation</h3>
          {/* Simulation d'une évaluation reçue, le GET n'est pas dans le contrat impose mais c'est requis */}
          <p><em>En attente de relecture par un pair... (Le nom du relecteur restera anonyme)</em></p>
        </section>
      )}
    </div>
  );
}
