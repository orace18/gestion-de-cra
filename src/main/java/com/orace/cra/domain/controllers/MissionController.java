package com.orace.cra.domain.controllers;

import com.orace.cra.domain.model.dtos.request.AssignmentRequest;
import com.orace.cra.domain.model.dtos.request.MissionRequest;
import com.orace.cra.domain.model.dtos.response.MissionResponse;
import com.orace.cra.domain.services.MissionService;
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
@RequestMapping("/api/missions")
@RequiredArgsConstructor
@Tag(name = "Missions", description = "Gestion des missions et affectations (Admin)")
public class MissionController {

    private final MissionService missionService;

    @Operation(summary = "Lister toutes les missions")
    @ApiResponse(responseCode = "200", description = "Liste retournée avec succès")
    @GetMapping
    public ResponseEntity<List<MissionResponse>> getAll() {
        return ResponseEntity.ok(missionService.getAll());
    }

    @Operation(summary = "Récupérer une mission par ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Mission trouvée"),
            @ApiResponse(responseCode = "400", description = "Mission introuvable")
    })
    @GetMapping("/{id}")
    public ResponseEntity<MissionResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(missionService.getById(id));
    }

    @Operation(summary = "Créer une mission")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Mission créée"),
            @ApiResponse(responseCode = "400", description = "Données invalides")
    })
    @PostMapping
    public ResponseEntity<MissionResponse> create(@RequestBody @Valid MissionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(missionService.create(request));
    }

    @Operation(summary = "Modifier une mission")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Mission mise à jour"),
            @ApiResponse(responseCode = "400", description = "Mission introuvable ou données invalides")
    })
    @PutMapping("/{id}")
    public ResponseEntity<MissionResponse> update(
            @PathVariable Long id,
            @RequestBody @Valid MissionRequest request) {
        return ResponseEntity.ok(missionService.update(id, request));
    }

    @Operation(summary = "Supprimer une mission")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Mission supprimée"),
            @ApiResponse(responseCode = "400", description = "Mission introuvable")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        missionService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Affecter une mission à un collaborateur")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Affectation créée"),
            @ApiResponse(responseCode = "400", description = "Collaborateur ou mission introuvable")
    })
    @PostMapping("/assign")
    public ResponseEntity<Void> assign(@RequestBody @Valid AssignmentRequest request) {
        missionService.assign(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
