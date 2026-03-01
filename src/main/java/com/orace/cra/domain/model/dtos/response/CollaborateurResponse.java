package com.orace.cra.domain.model.dtos.response;

import com.orace.cra.domain.model.enums.ContratType;
import com.orace.cra.domain.model.enums.Seniorite;
import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class CollaborateurResponse {
    private Long id;
    private String nom;
    private String prenom;
    private String email;
    private ContratType contrat;
    private Seniorite seniorite;
    private String salaire;
    private boolean actif;
}

