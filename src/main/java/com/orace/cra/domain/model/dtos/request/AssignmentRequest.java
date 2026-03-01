package com.orace.cra.domain.model.dtos.request;


import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class AssignmentRequest {
    @NotNull
    private Long collaborateurId;
    @NotNull
    private Long missionId;
    @NotNull
    private LocalDate dateDebut;
    private LocalDate dateFin;
}
