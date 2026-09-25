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
    <div>
      <h2>👨‍🏫 Espace Formateur</h2>

      {!session ? (
        <section className="card">
          <h3>Ouvrir une session</h3>
          <form onSubmit={ouvrirSession}>
            <div className="form-group">
              <label>Titre de la session : </label>
              <input 
                type="text" 
                value={titre} 
                onChange={e => setTitre(e.target.value)} 
                placeholder="Ex: TP Programmation Avancée"
                required 
              />
            </div>
            <div className="form-group">
              <label>Promotion : </label>
              <select value={promotionId} onChange={e => setPromotionId(e.target.value ? Number(e.target.value) : '')} required>
                <option value="">-- Choisir la promotion --</option>
                {promotions.map(p => <option key={p.id} value={p.id}>{p.nom}</option>)}
              </select>
            </div>
            <button type="submit" className="btn" disabled={loading}>
              {loading ? 'Ouverture en cours...' : 'Ouvrir la session'}
            </button>
            {error && <div className="alert alert-error">{error}</div>}
          </form>
        </section>
      ) : (
        <>
          <section className="card" style={{ borderTop: '4px solid var(--success)' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1rem' }}>
              <div>
                <h3 style={{ border: 'none', padding: 0, margin: 0 }}>Session : {titre}</h3>
                <p style={{ color: 'var(--text-muted)', margin: 0 }}>Expire à : {new Date(session.expirationAt).toLocaleTimeString()}</p>
              </div>
              <button className="btn btn-danger" style={{ width: 'auto' }} onClick={cloturerSession}>
                Clôturer la session
              </button>
            </div>
            <div className="text-center" style={{ margin: '2rem 0' }}>
              <p style={{ fontSize: '1.2rem', marginBottom: '0.5rem' }}>Code de présence à partager :</p>
              <div className="code-display">{session.code}</div>
            </div>
          </section>

          <div style={{ display: 'grid', gridTemplateColumns: '1fr 2fr', gap: '1.5rem' }}>
            <section className="card">
              <h3>Présence manuelle (Q14)</h3>
              <form onSubmit={ajouterPresenceManuelle}>
                <div className="form-group">
                  <label>Étudiant :</label>
                  <select value={etudiantIdManuel} onChange={e => setEtudiantIdManuel(e.target.value ? Number(e.target.value) : '')} required>
                    <option value="">-- Sélectionner --</option>
                    {etudiantsMocks.map(e => <option key={e.id} value={e.id}>{e.nom}</option>)}
                  </select>
                </div>
                <button type="submit" className="btn">Forcer la présence</button>
              </form>
            </section>

            <section className="card">
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <h3 style={{ border: 'none', margin: 0, padding: 0 }}>Tableau de bord</h3>
                <button 
                  className="btn" 
                  style={{ width: 'auto', padding: '0.4rem 0.8rem', fontSize: '0.85rem' }} 
                  onClick={() => chargerTableau(Number(promotionId))}
                >
                  🔄 Rafraîchir
                </button>
              </div>
              
              <div style={{ overflowX: 'auto' }}>
                <table>
                  <thead>
                    <tr>
                      <th>Étudiant</th>
                      <th className="text-center">Présences</th>
                      <th className="text-center">Exercices</th>
                      <th className="text-center">Moyenne</th>
                      <th className="text-center">À relire</th>
                    </tr>
                  </thead>
                  <tbody>
                    {tableau.length === 0 ? (
                      <tr><td colSpan={5} className="text-center" style={{ color: 'var(--text-muted)' }}>Aucune donnée</td></tr>
                    ) : (
                      tableau.map(row => (
                        <tr key={row.etudiantId}>
                          <td><strong>{row.nom}</strong></td>
                          <td className="text-center">
                            <span style={{ color: row.presences ? 'var(--success)' : 'var(--text-muted)' }}>
                              {row.presences ? '✓ Présent' : 'Absent'}
                            </span>
                          </td>
                          <td className="text-center">{row.exercicesDeposes}</td>
                          <td className="text-center">
                            <span style={{ fontWeight: 'bold', color: row.moyenne ? 'var(--primary)' : 'inherit' }}>
                              {row.moyenne !== null ? `${row.moyenne}/20` : '-'}
                            </span>
                          </td>
                          <td className="text-center">{row.relecturesEnAttente}</td>
                        </tr>
                      ))
                    )}
                  </tbody>
                </table>
              </div>
            </section>
          </div>
        </>
      )}
    </div>
  );
}
