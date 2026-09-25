package com.kfokam48.presencerelecture.repository;

import com.kfokam48.presencerelecture.domain.Presence;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PresenceRepository extends JpaRepository<Presence, Long> {
    boolean existsBySessionIdAndEtudiantId(Long sessionId, Long etudiantId);
}
