import { useState } from 'react';
import { api } from '../../services/api';

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
      setSessionId(res.sessionId);
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
    <div>
      <h2>🎓 Espace Étudiant</h2>

      {/* SECTION PRÉSENCE */}
      <section className="card">
        <h3>1. Marquer ma présence</h3>
        <form onSubmit={handlePresence}>
          <div className="form-group">
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

          <div className="form-group">
            <label>Code session : </label>
            <input 
              type="text" 
              value={code} 
              onChange={e => setCode(e.target.value)}
              disabled={successPresence}
              required
              placeholder="Ex: X1Y2Z"
            />
          </div>

          {!successPresence && (
            <button type="submit" className="btn" disabled={loadingPresence}>
              {loadingPresence ? 'Validation...' : 'Valider Présence'}
            </button>
          )}

          {errorPresence && <div className="alert alert-error">{errorPresence}</div>}
          {successPresence && <div className="alert alert-success">✓ Présence validée avec succès !</div>}
        </form>
      </section>

      {/* SECTION DÉPÔT (visible seulement si présent) */}
      {successPresence && (
        <section className="card">
          <h3>2. Déposer mon exercice</h3>
          <form onSubmit={handleDepot}>
            <div className="form-group">
              <label>Lien de l'exercice (URL) : </label>
              <input 
                type="url" 
                value={lien} 
                onChange={e => setLien(e.target.value)}
                required
                placeholder="https://github.com/..."
              />
            </div>

            <button type="submit" className="btn" disabled={loadingExercice}>
              {loadingExercice ? 'Dépôt...' : 'Déposer mon travail'}
            </button>

            {errorExercice && <div className="alert alert-error">{errorExercice}</div>}
            {successExercice && <div className="alert alert-success">✓ Exercice déposé et prêt pour la relecture !</div>}
          </form>
        </section>
      )}

      {/* SECTION EVALUATION */}
      {successExercice && (
        <section className="card" style={{ borderLeft: '4px solid var(--primary)' }}>
          <h3>3. Mon Évaluation</h3>
          {/* Simulation d'une évaluation reçue, le GET n'est pas dans le contrat impose mais c'est requis */}
          <div style={{ backgroundColor: 'var(--bg-color)', padding: '1rem', borderRadius: 'var(--radius)' }}>
            <p style={{ margin: 0, color: 'var(--text-muted)' }}>
              <em>En attente de relecture par un pair... (L'identité du relecteur restera anonyme)</em>
            </p>
          </div>
        </section>
      )}
    </div>
  );
}
