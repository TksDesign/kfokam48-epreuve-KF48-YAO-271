import { useState } from 'react';
import { api } from '../../services/api';

export default function EcranRelecteur() {
  const [relecteurId, setRelecteurId] = useState<number | ''>('');
  const [exerciceId, setExerciceId] = useState<number | ''>('');
  const [note, setNote] = useState<number | ''>('');
  const [commentaire, setCommentaire] = useState('');
  
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState(false);

  // Mocks pour Q1 et les exercices
  const etudiantsMocks = [
    { id: 1, nom: 'Alice Martin' },
    { id: 2, nom: 'Bob Dupuis' },
    { id: 3, nom: 'Charlie Legrand' },
  ];

  const exercicesMocks = [
    { id: 1, lien: 'https://github.com/exo1' },
    { id: 2, lien: 'https://github.com/exo2' },
  ];

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!relecteurId || !exerciceId || note === '' || !commentaire) return;

    setLoading(true);
    setError(null);
    setSuccess(false);

    try {
      await api.rendreRelecture(Number(exerciceId), Number(note), commentaire, Number(relecteurId));
      setSuccess(true);
    } catch (err: any) {
      setError(err.message || 'Erreur lors de l\'envoi de la relecture');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{ maxWidth: 400, margin: '0 auto', padding: '1rem' }}>
      <h2>Espace Relecteur</h2>

      <section style={{ border: '1px solid #ccc', padding: '1rem' }}>
        <h3>Évaluer un pair</h3>
        
        {success ? (
          <div style={{ color: 'green' }}>
            <p>✓ Relecture envoyée ! (Définitive)</p>
          </div>
        ) : (
          <form onSubmit={handleSubmit}>
            <div style={{ marginBottom: '1rem' }}>
              <label>Qui êtes-vous ? </label>
              <select 
                value={relecteurId} 
                onChange={e => setRelecteurId(e.target.value ? Number(e.target.value) : '')}
                required
              >
                <option value="">-- Choisir mon nom --</option>
                {etudiantsMocks.map(e => (
                  <option key={e.id} value={e.id}>{e.nom}</option>
                ))}
              </select>
            </div>

            <div style={{ marginBottom: '1rem' }}>
              <label>Exercice assigné : </label>
              <select 
                value={exerciceId} 
                onChange={e => setExerciceId(e.target.value ? Number(e.target.value) : '')}
                required
              >
                <option value="">-- Choisir un exercice --</option>
                {exercicesMocks.map(exo => (
                  <option key={exo.id} value={exo.id}>Exercice #{exo.id} - {exo.lien}</option>
                ))}
              </select>
            </div>

            <div style={{ marginBottom: '1rem' }}>
              <label>Note (0-20) : </label>
              <input 
                type="number" 
                min="0" max="20" 
                value={note} 
                onChange={e => setNote(e.target.value ? Number(e.target.value) : '')}
                required
              />
            </div>

            <div style={{ marginBottom: '1rem' }}>
              <label>Commentaire : </label>
              <textarea 
                value={commentaire} 
                onChange={e => setCommentaire(e.target.value)}
                required
                style={{ width: '100%', minHeight: '80px' }}
              />
            </div>

            <button type="submit" disabled={loading}>
              {loading ? 'Envoi...' : 'Valider définitivement'}
            </button>

            {error && <p style={{ color: 'red', marginTop: '1rem' }}>{error}</p>}
          </form>
        )}
      </section>
    </div>
  );
}
