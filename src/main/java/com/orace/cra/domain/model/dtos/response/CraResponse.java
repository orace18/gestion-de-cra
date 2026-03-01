package com.orace.cra.domain.model.dtos.response;

import com.orace.cra.domain.model.enums.CraStatus;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CraResponse {
    private Long id;
    private int mois;
    private int annee;
    private CraStatus statut;
    private String motifRejet;
    private String collaborateurNom;
    private String collaborateurPrenom;
    private List<CraDayResponse> jours;
}
