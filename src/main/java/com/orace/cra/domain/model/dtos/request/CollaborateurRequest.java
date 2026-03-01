package com.orace.cra.domain.model.dtos.request;


import com.orace.cra.domain.model.enums.ContratType;
import com.orace.cra.domain.model.enums.Seniorite;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CollaborateurRequest {
    @NotBlank
    private String nom;
    @NotBlank
    private String prenom;
    @Email
    @NotBlank
    private String email;
    private ContratType contrat;
    private Seniorite seniorite;
    private String salaire;
}
