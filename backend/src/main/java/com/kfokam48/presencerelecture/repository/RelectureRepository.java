package com.kfokam48.presencerelecture.repository;

import com.kfokam48.presencerelecture.domain.Relecture;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RelectureRepository extends JpaRepository<Relecture, Long> {
    boolean existsByExerciceId(Long exerciceId);
}
