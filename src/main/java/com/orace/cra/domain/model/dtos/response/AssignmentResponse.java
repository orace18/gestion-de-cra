package com.orace.cra.domain.model.dtos.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class AssignmentResponse {
    private Long id;
    private Long collaborateurId;
    private String collaborateurNom;
    private String collaborateurPrenom;
    private Long missionId;
    private String missionTitre;
    private LocalDate dateDebut;
    private LocalDate dateFin;
}
