package com.orace.cra.domain.model.repository;

import com.orace.cra.domain.model.entities.Cra;
import com.orace.cra.domain.model.enums.CraStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CraRepository extends JpaRepository<Cra, Long> {
    Optional<Cra> findByCollaborateurIdAndMoisAndAnnee(Long userId, int mois, int annee);
    List<Cra> findByStatut(CraStatus statut);
    List<Cra> findByCollaborateurId(Long userId);

    List<Cra> findByStatutIn(List<CraStatus> statuts);
}
