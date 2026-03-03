package com.orace.cra.domain.controllers;

import com.orace.cra.domain.model.dtos.request.CollaborateurRequest;
import com.orace.cra.domain.model.dtos.response.CollaborateurResponse;
import com.orace.cra.domain.services.CollaborateurService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/collaborateurs")
@RequiredArgsConstructor
@Tag(name = "Collaborateurs", description = "Gestion des collaborateurs (Admin)")
public class CollaborateurController {

    private final CollaborateurService collaborateurService;

    @Operation(summary = "Lister tous les collaborateurs")
    @ApiResponse(responseCode = "200", description = "Liste retournée avec succès")
    @GetMapping
    public ResponseEntity<List<CollaborateurResponse>> getAll() {
        return ResponseEntity.ok(collaborateurService.getAll());
    }

    @Operation(summary = "Récupérer un collaborateur par ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Collaborateur trouvé"),
            @ApiResponse(responseCode = "400", description = "Collaborateur introuvable")
    })
    @GetMapping("/{id}")
    public ResponseEntity<CollaborateurResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(collaborateurService.getById(id));
    }

    @Operation(summary = "Créer un collaborateur")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Collaborateur créé"),
            @ApiResponse(responseCode = "400", description = "Données invalides ou email déjà utilisé")
    })
    @PostMapping
    public ResponseEntity<CollaborateurResponse> create(@RequestBody @Valid CollaborateurRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(collaborateurService.create(request));
    }

    @Operation(summary = "Modifier un collaborateur (sauf email)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Collaborateur mis à jour"),
            @ApiResponse(responseCode = "400", description = "Collaborateur introuvable ou données invalides")
    })
    @PutMapping("/{id}")
    public ResponseEntity<CollaborateurResponse> update(
            @PathVariable Long id,
            @RequestBody @Valid CollaborateurRequest request) {
        return ResponseEntity.ok(collaborateurService.update(id, request));
    }

    @Operation(summary = "Activer / désactiver un collaborateur")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Statut modifié"),
            @ApiResponse(responseCode = "400", description = "Collaborateur introuvable")
    })
    @PatchMapping("/{id}/activation")
    public ResponseEntity<CollaborateurResponse> toggleActivation(@PathVariable Long id) {
        return ResponseEntity.ok(collaborateurService.toggleActivation(id));
    }
}
