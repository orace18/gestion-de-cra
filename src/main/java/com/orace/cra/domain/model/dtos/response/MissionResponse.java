package com.orace.cra.domain.model.dtos.response;


import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;

@Data
@Builder
public class MissionResponse {
    private Long id;
    private String titre;
    private String description;
    private String motif;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private Double tjm;
}
