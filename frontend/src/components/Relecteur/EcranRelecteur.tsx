import { useState, useEffect } from 'react';
import { api, ExerciceAEvaluer } from '../../services/api';

export default function EcranRelecteur() {
  const [relecteurId, setRelecteurId] = useState<number | ''>('');
  const [exerciceId, setExerciceId] = useState<number | ''>('');
  const [note, setNote] = useState<number | ''>('');
  const [commentaire, setCommentaire] = useState('');

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState(false);

  const [exercices, setExercices] = useState<ExerciceAEvaluer[]>([]);
  const [loadingExercices, setLoadingExercices] = useState(false);

  // Mocks pour Q1 uniquement (pas d'endpoint contrat pour lister les etudiants)
  const etudiantsMocks = [
    { id: 1, nom: 'Alice Martin' },
    { id: 2, nom: 'Bob Dupuis' },
    { id: 3, nom: 'Charlie Legrand' },
  ];

  // Charge la vraie liste des exercices assignes des que le relecteur est choisi
  useEffect(() => {
    if (!relecteurId) {
      setExercices([]);
      return;
    }
    setLoadingExercices(true);
    setExerciceId('');
    api.exercicesAEvaluer(Number(relecteurId))
      .then(setExercices)
      .catch(() => setExercices([]))
      .finally(() => setLoadingExercices(false));
  }, [relecteurId]);

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
    <div>
      <h2>✍️ Espace Relecteur</h2>

      <section className="card" style={{ maxWidth: '600px', margin: '0 auto' }}>
        <h3>Évaluer un pair</h3>
        <p style={{ color: 'var(--text-muted)', fontSize: '0.9rem', marginBottom: '1.5rem' }}>
          Vous avez été assigné pour évaluer le travail d'un autre étudiant. Soyez constructif !
        </p>
        
        {success ? (
          <div className="alert alert-success">
            <strong>✓ Relecture envoyée avec succès !</strong>
            <p style={{ margin: '0.5rem 0 0 0', fontSize: '0.9rem' }}>
              Cette évaluation est définitive et a été transmise de manière anonyme.
            </p>
          </div>
        ) : (
          <form onSubmit={handleSubmit}>
            <div className="form-group">
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

            <div className="form-group">
              <label>Exercice assigné : </label>
              <select
                value={exerciceId}
                onChange={e => setExerciceId(e.target.value ? Number(e.target.value) : '')}
                required
                disabled={!relecteurId || loadingExercices}
              >
                <option value="">
                  {!relecteurId
                    ? '-- Choisissez d\'abord qui vous êtes --'
                    : loadingExercices
                    ? 'Chargement...'
                    : exercices.length === 0
                    ? 'Aucun exercice à évaluer pour le moment'
                    : '-- Sélectionner l\'exercice --'}
                </option>
                {exercices.map(exo => (
                  <option key={exo.id} value={exo.id}>Exercice #{exo.id} - {exo.lien}</option>
                ))}
              </select>
            </div>

            <div className="form-group" style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1rem' }}>
              <div>
                <label>Note (0-20) : </label>
                <input 
                  type="number" 
                  min="0" max="20" 
                  value={note} 
                  onChange={e => setNote(e.target.value ? Number(e.target.value) : '')}
                  required
                  placeholder="Ex: 15"
                />
              </div>
            </div>

            <div className="form-group">
              <label>Commentaire constructif : </label>
              <textarea 
                value={commentaire} 
                onChange={e => setCommentaire(e.target.value)}
                required
                placeholder="Expliquez votre note de manière bienveillante..."
                style={{ minHeight: '120px' }}
              />
            </div>

            <button type="submit" className="btn" disabled={loading}>
              {loading ? 'Envoi en cours...' : 'Valider définitivement l\'évaluation'}
            </button>

            {error && <div className="alert alert-error">{error}</div>}
          </form>
        )}
      </section>
    </div>
  );
}
