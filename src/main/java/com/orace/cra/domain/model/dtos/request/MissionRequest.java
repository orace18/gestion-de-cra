package com.orace.cra.domain.model.dtos.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class MissionRequest {
    @NotBlank
    private String titre;
    private String description;
    private String motif;
    @NotNull
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private Double tjm;
}
