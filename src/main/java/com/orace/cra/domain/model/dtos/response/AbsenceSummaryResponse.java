package com.orace.cra.domain.model.dtos.response;

import lombok.Builder;
import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class AbsenceSummaryResponse {
    private Long collaborateurId;
    private String collaborateurNom;
    private String collaborateurPrenom;
    private int annee;
    private List<AbsenceResponse> absences;
    private Map<String, Double> totauxParType;
    private Double totalJours;
}