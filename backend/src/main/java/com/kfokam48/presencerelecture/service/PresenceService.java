package com.kfokam48.presencerelecture.service;

import com.kfokam48.presencerelecture.domain.Presence;
import com.kfokam48.presencerelecture.domain.Session;
import com.kfokam48.presencerelecture.domain.exception.CodeExpireException;
import com.kfokam48.presencerelecture.domain.exception.CodeInconnuException;
import com.kfokam48.presencerelecture.domain.exception.DejaPresentException;
import com.kfokam48.presencerelecture.domain.exception.EtudiantInconnuException;
import com.kfokam48.presencerelecture.domain.exception.SessionInconnueException;
import com.kfokam48.presencerelecture.domain.exception.TropDeTentativesException;
import com.kfokam48.presencerelecture.repository.EtudiantRepository;
import com.kfokam48.presencerelecture.repository.PresenceRepository;
import com.kfokam48.presencerelecture.repository.SessionRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.util.Optional;

@Service
public class PresenceService {

    private final SessionRepository sessionRepository;
    private final PresenceRepository presenceRepository;
    private final EtudiantRepository etudiantRepository;
    private final TentativesCodeTracker tentativesCodeTracker;
    private final Clock clock;

    public PresenceService(SessionRepository sessionRepository, PresenceRepository presenceRepository,
                            EtudiantRepository etudiantRepository, TentativesCodeTracker tentativesCodeTracker, Clock clock) {
        this.sessionRepository = sessionRepository;
        this.presenceRepository = presenceRepository;
        this.etudiantRepository = etudiantRepository;
        this.tentativesCodeTracker = tentativesCodeTracker;
        this.clock = clock;
    }

    /** EF1 : l'etudiant marque sa presence avec un code. */
    @Transactional
    public Presence marquerAvecCode(String code, Long etudiantId) {
        if (tentativesCodeTracker.estBloque(etudiantId)) {
            throw new TropDeTentativesException();
        }

        Optional<Session> sessionTrouvee = sessionRepository.findByCode(code);
        if (sessionTrouvee.isEmpty()) {
            tentativesCodeTracker.enregistrerEchec(etudiantId);
            throw new CodeInconnuException();
        }
        Session session = sessionTrouvee.get();

        if (session.estExpiree(clock.instant())) {
            throw new CodeExpireException();
        }
        if (presenceRepository.existsBySessionIdAndEtudiantId(session.getId(), etudiantId)) {
            throw new DejaPresentException();
        }

        Presence presence = new Presence(clock.instant(), Presence.Source.ETUDIANT, session.getId(), etudiantId);
        try {
            presence = presenceRepository.save(presence);
        } catch (DataIntegrityViolationException e) {
            // Issue #37 : deux requetes concurrentes passent toutes les deux le
            // check exists() avant que l'une des deux n'ait commit - la
            // contrainte uq_presence_session_etudiant protege l'integrite,
            // on traduit sa violation en 409 propre plutot que 500.
            throw new DejaPresentException();
        }
        tentativesCodeTracker.reinitialiser(etudiantId);
        return presence;
    }

    /** EF7/RG8 (issue #4) : le formateur ajoute une presence sans code, source=FORMATEUR. */
    @Transactional
    public Presence marquerManuelle(Long sessionId, Long etudiantId) {
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(SessionInconnueException::new);

        if (!etudiantRepository.existsById(etudiantId)) {
            throw new EtudiantInconnuException();
        }

        if (presenceRepository.existsBySessionIdAndEtudiantId(session.getId(), etudiantId)) {
            throw new DejaPresentException();
        }

        Presence presence = new Presence(clock.instant(), Presence.Source.FORMATEUR, session.getId(), etudiantId);
        try {
            return presenceRepository.save(presence);
        } catch (DataIntegrityViolationException e) {
            // Meme protection concurrente qu'en #37 : le formateur pourrait
            // double-cliquer, ou un etudiant marquer sa presence en meme
            // temps que le formateur l'ajoute manuellement.
            throw new DejaPresentException();
        }
    }
}
