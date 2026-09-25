package com.kfokam48.presencerelecture.service;

import com.kfokam48.presencerelecture.domain.Presence;
import com.kfokam48.presencerelecture.domain.Session;
import com.kfokam48.presencerelecture.domain.exception.CodeExpireException;
import com.kfokam48.presencerelecture.domain.exception.CodeInconnuException;
import com.kfokam48.presencerelecture.domain.exception.DejaPresentException;
import com.kfokam48.presencerelecture.domain.exception.TropDeTentativesException;
import com.kfokam48.presencerelecture.repository.PresenceRepository;
import com.kfokam48.presencerelecture.repository.SessionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.util.Optional;

@Service
public class PresenceService {

    private final SessionRepository sessionRepository;
    private final PresenceRepository presenceRepository;
    private final TentativesCodeTracker tentativesCodeTracker;
    private final Clock clock;

    public PresenceService(SessionRepository sessionRepository, PresenceRepository presenceRepository,
                            TentativesCodeTracker tentativesCodeTracker, Clock clock) {
        this.sessionRepository = sessionRepository;
        this.presenceRepository = presenceRepository;
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
        presence = presenceRepository.save(presence);
        tentativesCodeTracker.reinitialiser(etudiantId);
        return presence;
    }
}
