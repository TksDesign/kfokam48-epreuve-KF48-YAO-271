package com.kfokam48.presencerelecture.web;

import com.kfokam48.presencerelecture.domain.Presence;
import com.kfokam48.presencerelecture.service.PresenceService;
import com.kfokam48.presencerelecture.web.dto.MarquerPresenceRequest;
import com.kfokam48.presencerelecture.web.dto.PresenceResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/presences")
public class PresenceController {

    private final PresenceService presenceService;

    public PresenceController(PresenceService presenceService) {
        this.presenceService = presenceService;
    }

    @PostMapping
    public ResponseEntity<PresenceResponse> marquer(@Valid @RequestBody MarquerPresenceRequest requete) {
        Presence presence = presenceService.marquerAvecCode(requete.code(), requete.etudiantId());
        return ResponseEntity.status(HttpStatus.CREATED).body(PresenceResponse.depuis(presence));
    }
}
