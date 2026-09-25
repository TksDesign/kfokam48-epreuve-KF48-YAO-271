package com.kfokam48.presencerelecture.web;

import com.kfokam48.presencerelecture.domain.Presence;
import com.kfokam48.presencerelecture.domain.Session;
import com.kfokam48.presencerelecture.service.PresenceService;
import com.kfokam48.presencerelecture.service.SessionService;
import com.kfokam48.presencerelecture.web.dto.OuvrirSessionRequest;
import com.kfokam48.presencerelecture.web.dto.PresenceManuelleRequest;
import com.kfokam48.presencerelecture.web.dto.PresenceResponse;
import com.kfokam48.presencerelecture.web.dto.SessionResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/sessions")
public class SessionController {

    private final SessionService sessionService;
    private final PresenceService presenceService;

    public SessionController(SessionService sessionService, PresenceService presenceService) {
        this.sessionService = sessionService;
        this.presenceService = presenceService;
    }

    @PostMapping
    public ResponseEntity<SessionResponse> ouvrir(@Valid @RequestBody OuvrirSessionRequest requete) {
        Session session = sessionService.ouvrir(requete.titre(), requete.promotionId());
        return ResponseEntity.status(HttpStatus.CREATED).body(SessionResponse.depuis(session));
    }

    @PutMapping("/{id}/cloture")
    public ResponseEntity<Void> clore(@PathVariable Long id) {
        sessionService.cloturer(id);
        return ResponseEntity.ok().build();
    }

    /** Issue #4, EF7/RG8. */
    @PostMapping("/{id}/presences-manuelles")
    public ResponseEntity<PresenceResponse> ajouterPresenceManuelle(@PathVariable Long id,
                                                                      @Valid @RequestBody PresenceManuelleRequest requete) {
        Presence presence = presenceService.marquerManuelle(id, requete.etudiantId());
        return ResponseEntity.status(HttpStatus.CREATED).body(PresenceResponse.depuis(presence));
    }
}
