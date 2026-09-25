import { useState } from 'react';
import { api, Session } from '../../services/api';

export default function EcranFormateur() {
  const [titre, setTitre] = useState('');
  const [promotionId, setPromotionId] = useState<number | ''>('');
  const [session, setSession] = useState<Session | null>(null);
  const [tableau, setTableau] = useState<any[]>([]);
  
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  
  const [etudiantIdManuel, setEtudiantIdManuel] = useState<number | ''>('');

  const promotions = [
    { id: 1, nom: 'Promo 2026' }
  ];
  const etudiantsMocks = [
    { id: 1, nom: 'Alice Martin' },
    { id: 2, nom: 'Bob Dupuis' },
    { id: 3, nom: 'Charlie Legrand' },
  ];

  const ouvrirSession = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!titre || !promotionId) return;

    setLoading(true);
    setError(null);
    try {
      const res = await api.ouvrirSession(titre, Number(promotionId));
      setSession(res);
      chargerTableau(Number(promotionId));
    } catch (err: any) {
      setError(err.message || 'Erreur lors de l\'ouverture');
    } finally {
      setLoading(false);
    }
  };

  const chargerTableau = async (promoId: number) => {
    try {
      const data = await api.getTableau(promoId);
      setTableau(data);
    } catch (err: any) {
      console.error(err);
    }
  };

  const ajouterPresenceManuelle = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!session || !etudiantIdManuel) return;

    try {
      await api.ajouterPresenceManuelle(session.id, Number(etudiantIdManuel));
      alert('Présence ajoutée avec succès !');
      chargerTableau(Number(promotionId));
    } catch (err: any) {
      alert(err.message || 'Erreur lors de l\'ajout manuel');
    }
  };

  const cloturerSession = async () => {
    if (!session) return;
    try {
      await api.cloturerSession(session.id);
      alert('Session clôturée !');
    } catch (err: any) {
      alert(err.message);
    }
  };

  return (
    <div style={{ maxWidth: 800, margin: '0 auto', padding: '1rem' }}>
      <h2>Espace Formateur</h2>

      {!session ? (
        <section style={{ border: '1px solid #ccc', padding: '1rem' }}>
          <h3>Ouvrir une session</h3>
          <form onSubmit={ouvrirSession}>
            <div style={{ marginBottom: '1rem' }}>
              <label>Titre : </label>
              <input type="text" value={titre} onChange={e => setTitre(e.target.value)} required />
            </div>
            <div style={{ marginBottom: '1rem' }}>
              <label>Promotion : </label>
              <select value={promotionId} onChange={e => setPromotionId(e.target.value ? Number(e.target.value) : '')} required>
                <option value="">-- Choisir --</option>
                {promotions.map(p => <option key={p.id} value={p.id}>{p.nom}</option>)}
              </select>
            </div>
            <button type="submit" disabled={loading}>
              {loading ? 'Ouverture...' : 'Ouvrir'}
            </button>
            {error && <p style={{ color: 'red' }}>{error}</p>}
          </form>
        </section>
      ) : (
        <>
          <section style={{ border: '1px solid #ccc', padding: '1rem', backgroundColor: '#eef' }}>
            <h3>Session : {titre}</h3>
            <p><strong>Code de présence : </strong> <span style={{ fontSize: '1.5em', letterSpacing: '2px' }}>{session.code}</span></p>
            <p>Expire à : {new Date(session.expirationAt).toLocaleTimeString()}</p>
            <button onClick={cloturerSession} style={{ background: 'red', color: 'white' }}>Clôturer la session</button>
          </section>

          <section style={{ border: '1px solid #ccc', padding: '1rem', marginTop: '1rem' }}>
            <h3>Ajouter présence manuellement (Q14)</h3>
            <form onSubmit={ajouterPresenceManuelle}>
              <select value={etudiantIdManuel} onChange={e => setEtudiantIdManuel(e.target.value ? Number(e.target.value) : '')} required>
                <option value="">-- Choisir un étudiant --</option>
                {etudiantsMocks.map(e => <option key={e.id} value={e.id}>{e.nom}</option>)}
              </select>
              <button type="submit">Ajouter</button>
            </form>
          </section>

          <section style={{ border: '1px solid #ccc', padding: '1rem', marginTop: '1rem' }}>
            <h3>Tableau de bord</h3>
            <button onClick={() => chargerTableau(Number(promotionId))}>Rafraîchir</button>
            <table style={{ width: '100%', marginTop: '1rem', borderCollapse: 'collapse' }}>
              <thead>
                <tr>
                  <th style={{ border: '1px solid #ccc', padding: '8px' }}>Étudiant</th>
                  <th style={{ border: '1px solid #ccc', padding: '8px' }}>Présences</th>
                  <th style={{ border: '1px solid #ccc', padding: '8px' }}>Exercices</th>
                  <th style={{ border: '1px solid #ccc', padding: '8px' }}>Moyenne</th>
                  <th style={{ border: '1px solid #ccc', padding: '8px' }}>À relire</th>
                </tr>
              </thead>
              <tbody>
                {tableau.map(row => (
                  <tr key={row.etudiantId}>
                    <td style={{ border: '1px solid #ccc', padding: '8px' }}>{row.nom}</td>
                    <td style={{ border: '1px solid #ccc', padding: '8px', textAlign: 'center' }}>{row.presences}</td>
                    <td style={{ border: '1px solid #ccc', padding: '8px', textAlign: 'center' }}>{row.exercicesDeposes}</td>
                    <td style={{ border: '1px solid #ccc', padding: '8px', textAlign: 'center' }}>{row.moyenne !== null ? row.moyenne : '-'}</td>
                    <td style={{ border: '1px solid #ccc', padding: '8px', textAlign: 'center' }}>{row.relecturesEnAttente}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </section>
        </>
      )}
    </div>
  );
}
