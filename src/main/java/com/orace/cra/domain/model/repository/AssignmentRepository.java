package com.orace.cra.domain.model.repository;

import com.orace.cra.domain.model.entities.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Long> {
    List<Assignment> findByCollaborateurId(Long userId);

    Optional<Assignment> findByCollaborateurIdAndMissionId(Long userId, Long missionId);

    @Query("SELECT a FROM Assignment a WHERE a.collaborateur.id = :userId AND a.dateDebut <= :date AND (a.dateFin IS NULL OR a.dateFin >= :date)")
    Optional<Assignment> findActiveAssignment(@Param("userId") Long userId, @Param("date") LocalDate date);
}
