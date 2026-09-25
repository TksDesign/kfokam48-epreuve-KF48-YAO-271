package com.kfokam48.presencerelecture.web;

import com.kfokam48.presencerelecture.service.RelectureService;
import com.kfokam48.presencerelecture.web.dto.RendreRelectureRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/relectures")
public class RelectureController {

    private final RelectureService relectureService;

    public RelectureController(RelectureService relectureService) {
        this.relectureService = relectureService;
    }

    @PostMapping("/{id}")
    public ResponseEntity<Void> evaluer(@PathVariable Long id, @Valid @RequestBody RendreRelectureRequest requete) {
        relectureService.evaluer(id, requete.relecteurId(), requete.note(), requete.commentaire());
        return ResponseEntity.ok().build();
    }
}
