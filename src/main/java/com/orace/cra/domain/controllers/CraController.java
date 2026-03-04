package com.orace.cra.domain.controllers;


import com.orace.cra.domain.model.dtos.response.AbsenceSummaryResponse;
import com.orace.cra.domain.model.dtos.response.CraResponse;
import com.orace.cra.domain.model.dtos.request.CraValidationRequest;
import com.orace.cra.domain.model.dtos.request.RemplirJourRequest;
import com.orace.cra.domain.model.entities.User;
import com.orace.cra.domain.services.CraService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "CRA", description = "Gestion des Comptes Rendus d'Activité")
public class CraController {

    private final CraService craService;

    @Operation(summary = "Récupérer mon CRA d'un mois donné")
    @GetMapping("/cra")
    public ResponseEntity<CraResponse> getMyCra(
            @RequestParam int mois,
            @RequestParam int annee,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(craService.getMyCra(user.getId(), mois, annee));
    }

    @Operation(summary = "Récupérer tous mes CRA")
    @GetMapping("/cra/mes-cras")
    public ResponseEntity<List<CraResponse>> getMesCras(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(craService.getMesCras(user.getId()));
    }

    @Operation(summary = "Remplir tout le mois en 1 clic (tous les jours ouvrés = TRAVAILLE)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Mois rempli"),
            @ApiResponse(responseCode = "400", description = "CRA non modifiable")
    })
    @PutMapping("/cra/{id}/remplir-mois")
    public ResponseEntity<CraResponse> remplirMois(
            @PathVariable Long id,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(craService.remplirMois(id, user.getId()));
    }

    @Operation(summary = "Modifier un jour précis du CRA")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Jour mis à jour"),
            @ApiResponse(responseCode = "400", description = "CRA non modifiable ou données invalides")
    })
    @PutMapping("/cra/{id}/jour")
    public ResponseEntity<CraResponse> updateJour(
            @PathVariable Long id,
            @RequestBody @Valid RemplirJourRequest request,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(craService.updateJour(id, user.getId(), request));
    }

    @Operation(summary = "Soumettre mon CRA")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "CRA soumis"),
            @ApiResponse(responseCode = "400", description = "CRA vide ou non modifiable")
    })
    @PostMapping("/cra/{id}/soumettre")
    public ResponseEntity<CraResponse> soumettre(
            @PathVariable Long id,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(craService.soumettre(id, user.getId()));
    }

    @Operation(summary = "Lister tous les CRA soumis en attente de validation")
    @GetMapping("/admin/cra/a-valider")
    public ResponseEntity<List<CraResponse>> getCrasAValider() {
        return ResponseEntity.ok(craService.getCrasAValider());
    }

    @Operation(summary = "Lister tous les CRA")
    @GetMapping("/admin/cra")
    public ResponseEntity<List<CraResponse>> getAllCras() {
        return ResponseEntity.ok(craService.getAllCras());
    }

    @Operation(summary = "Valider un CRA")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "CRA validé"),
            @ApiResponse(responseCode = "400", description = "CRA non soumis")
    })
    @PostMapping("/admin/cra/{id}/approuver")
    public ResponseEntity<CraResponse> approuver(@PathVariable Long id) {
        return ResponseEntity.ok(craService.approuver(id));
    }

    @Operation(summary = "Rejeter un CRA (motif obligatoire)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "CRA rejeté"),
            @ApiResponse(responseCode = "400", description = "Motif manquant ou CRA non soumis")
    })
    @PostMapping("/admin/cra/{id}/rejeter")
    public ResponseEntity<CraResponse> rejeter(
            @PathVariable Long id,
            @RequestBody CraValidationRequest request) {
        return ResponseEntity.ok(craService.rejeter(id, request));
    }

    @Operation(summary = "Invalider un CRA approuvé (motif obligatoire)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "CRA invalidé"),
            @ApiResponse(responseCode = "400", description = "Motif manquant ou CRA non approuvé")
    })
    @PostMapping("/admin/cra/{id}/invalider")
    public ResponseEntity<CraResponse> invalider(
            @PathVariable Long id,
            @RequestBody CraValidationRequest request) {
        return ResponseEntity.ok(craService.invalider(id, request));
    }

    @Operation(summary = "Récupérer mes absences sur une année")
    @GetMapping("/cra/mes-absences")
    public ResponseEntity<AbsenceSummaryResponse> getMesAbsences(
            @RequestParam int annee,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(craService.getMesAbsences(user.getId(), annee));
    }

    @Operation(summary = "Récupérer les absences d'un collaborateur (Admin)")
    @GetMapping("/admin/collaborateurs/{id}/absences")
    public ResponseEntity<AbsenceSummaryResponse> getAbsencesCollaborateur(
            @PathVariable Long id,
            @RequestParam int annee) {
        return ResponseEntity.ok(craService.getAbsencesCollaborateur(id, annee));
    }
}