package com.kfokam48.presencerelecture.web;

import com.kfokam48.presencerelecture.domain.Exercice;
import com.kfokam48.presencerelecture.service.ExerciceService;
import com.kfokam48.presencerelecture.web.dto.DeposerExerciceRequest;
import com.kfokam48.presencerelecture.web.dto.ExerciceResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/exercices")
public class ExerciceController {

    private final ExerciceService exerciceService;

    public ExerciceController(ExerciceService exerciceService) {
        this.exerciceService = exerciceService;
    }

    @PostMapping
    public ResponseEntity<ExerciceResponse> deposer(@Valid @RequestBody DeposerExerciceRequest requete) {
        Exercice exercice = exerciceService.deposer(requete.sessionId(), requete.etudiantId(), requete.lien());
        return ResponseEntity.status(HttpStatus.CREATED).body(ExerciceResponse.depuis(exercice));
    }
}
