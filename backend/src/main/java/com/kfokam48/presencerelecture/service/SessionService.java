package com.kfokam48.presencerelecture.service;

import com.kfokam48.presencerelecture.domain.Session;
import com.kfokam48.presencerelecture.domain.exception.DejaClotureeException;
import com.kfokam48.presencerelecture.domain.exception.SessionInconnueException;
import com.kfokam48.presencerelecture.repository.SessionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;

@Service
public class SessionService {

    /** RG1 : le code de presence expire 15 minutes apres l'ouverture. */
    static final Duration DUREE_VALIDITE_CODE = Duration.ofMinutes(15);

    private static final String ALPHABET_CODE = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"; // sans 0/O/1/I, ambigus
    private static final int LONGUEUR_CODE = 6;

    private final SessionRepository sessionRepository;
    private final Clock clock;
    private final SecureRandom random = new SecureRandom();

    public SessionService(SessionRepository sessionRepository, Clock clock) {
        this.sessionRepository = sessionRepository;
        this.clock = clock;
    }

    @Transactional
    public Session ouvrir(String titre, Long promotionId) {
        Instant maintenant = clock.instant();
        Session session = new Session(titre, genererCodeUnique(), maintenant, maintenant.plus(DUREE_VALIDITE_CODE), promotionId);
        return sessionRepository.save(session);
    }

    @Transactional
    public void cloturer(Long sessionId) {
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(SessionInconnueException::new);
        if (session.isCloturee()) {
            throw new DejaClotureeException();
        }
        session.cloturer();
        sessionRepository.save(session);
    }

    private String genererCodeUnique() {
        String code;
        do {
            code = genererCode();
        } while (sessionRepository.existsByCode(code));
        return code;
    }

    private String genererCode() {
        StringBuilder sb = new StringBuilder(LONGUEUR_CODE);
        for (int i = 0; i < LONGUEUR_CODE; i++) {
            sb.append(ALPHABET_CODE.charAt(random.nextInt(ALPHABET_CODE.length())));
        }
        return sb.toString();
    }
}
