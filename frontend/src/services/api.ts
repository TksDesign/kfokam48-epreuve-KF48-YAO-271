export const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api';

// Définitions des types
export interface Session {
  id: number;
  code: string;
  ouvertureAt: string;
  expirationAt: string;
}

export interface Presence {
  id: number;
  sessionId: number;
  etudiantId: number;
  source: 'ETUDIANT' | 'FORMATEUR';
}

export interface Exercice {
  id: number;
  statut: string;
}

export interface ApiError {
  code: string;
  message: string;
}

// Activer le mode mock en attendant le backend de Claude. Désactivable via VITE_USE_MOCKS=false
const USE_MOCKS = import.meta.env.VITE_USE_MOCKS !== 'false'; 

// Helper pour gérer les réponses
async function fetchApi<T>(url: string, options?: RequestInit): Promise<T> {
  if (USE_MOCKS) {
    return new Promise((resolve, reject) => {
      setTimeout(() => {
        console.log(`[MOCK] ${options?.method || 'GET'} ${url}`, options?.body);
        if (url.includes('/presences')) {
          const body = JSON.parse(options?.body as string);
          if (body.code === 'EXPIRE') return reject({ code: 'CODE_EXPIRE', message: 'Le code a expiré' });
          resolve({ id: 1, sessionId: 101, etudiantId: body.etudiantId, source: 'ETUDIANT' } as any);
        }
        if (url.includes('/exercices')) {
          resolve({ id: 1, statut: 'DEPOSE' } as any);
        }
        if (url.includes('/sessions') && options?.method === 'POST') {
          if (url.includes('/presences-manuelles')) {
            resolve({ id: 2, sessionId: 101, etudiantId: JSON.parse(options.body as string).etudiantId, source: 'FORMATEUR' } as any);
          } else {
            resolve({ id: 101, code: 'X1Y2Z', ouvertureAt: new Date().toISOString(), expirationAt: new Date(Date.now() + 15*60000).toISOString() } as any);
          }
        }
        if (url.includes('/tableau')) {
          resolve([
            { etudiantId: 1, nom: 'Alice Martin', presences: 1, exercicesDeposes: 1, moyenne: 14.5, relecturesEnAttente: 0 },
            { etudiantId: 2, nom: 'Bob Dupuis', presences: 1, exercicesDeposes: 0, moyenne: null, relecturesEnAttente: 1 }
          ] as any);
        }
        resolve({} as any);
      }, 500);
    });
  }

  const response = await fetch(`${API_BASE_URL}${url}`, {
    ...options,
    headers: {
      'Content-Type': 'application/json',
      ...options?.headers,
    },
  });

  if (!response.ok) {
    let errorData: ApiError;
    try {
      errorData = await response.json();
    } catch {
      throw new Error(`Erreur réseau: ${response.statusText}`);
    }
    throw errorData; // On throw l'objet erreur du backend
  }

  // Certains endpoints comme PUT /cloture renvoient 200 sans body
  const text = await response.text();
  return text ? JSON.parse(text) : undefined;
}

// Couche API dédiée
export const api = {
  // --- Sessions ---
  ouvrirSession: (titre: string, promotionId: number) =>
    fetchApi<Session>('/sessions', {
      method: 'POST',
      body: JSON.stringify({ titre, promotionId }),
    }),
    
  cloturerSession: (id: number) =>
    fetchApi<void>(`/sessions/${id}/cloture`, {
      method: 'PUT',
    }),
    
  ajouterPresenceManuelle: (sessionId: number, etudiantId: number) =>
    fetchApi<Presence>(`/sessions/${sessionId}/presences-manuelles`, {
      method: 'POST',
      body: JSON.stringify({ etudiantId }),
    }),

  // --- Presences ---
  marquerPresence: (code: string, etudiantId: number) =>
    fetchApi<Presence>('/presences', {
      method: 'POST',
      body: JSON.stringify({ code, etudiantId }),
    }),

  // --- Exercices ---
  deposerExercice: (sessionId: number, etudiantId: number, lien: string) =>
    fetchApi<Exercice>('/exercices', {
      method: 'POST',
      body: JSON.stringify({ sessionId, etudiantId, lien }),
    }),

  // --- Relectures ---
  rendreRelecture: (exerciceId: number, note: number, commentaire: string) =>
    fetchApi<void>(`/relectures/${exerciceId}`, {
      method: 'POST',
      body: JSON.stringify({ note, commentaire }),
    }),

  // --- Tableau ---
  getTableau: (promotionId: number) =>
    fetchApi<any[]>(`/tableau?promotionId=${promotionId}`, {
      method: 'GET',
    }),
};
