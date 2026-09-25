package com.kfokam48.presencerelecture.repository;

import com.kfokam48.presencerelecture.domain.Etudiant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EtudiantRepository extends JpaRepository<Etudiant, Long> {
    List<Etudiant> findByPromotionId(Long promotionId);
}
