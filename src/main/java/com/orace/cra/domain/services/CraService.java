package com.orace.cra.domain.services;


import com.orace.cra.domain.model.dtos.response.AbsenceResponse;
import com.orace.cra.domain.model.dtos.response.AbsenceSummaryResponse;
import com.orace.cra.domain.model.dtos.response.CraDayResponse;
import com.orace.cra.domain.model.dtos.response.CraResponse;
import com.orace.cra.domain.model.dtos.request.CraValidationRequest;
import com.orace.cra.domain.model.dtos.request.RemplirJourRequest;
import com.orace.cra.domain.model.entities.Cra;
import com.orace.cra.domain.model.entities.CraDay;
import com.orace.cra.domain.model.entities.User;
import com.orace.cra.domain.model.enums.CraStatus;
import com.orace.cra.domain.model.enums.DayType;
import com.orace.cra.domain.model.repository.AssignmentRepository;
import com.orace.cra.domain.model.repository.CraRepository;
import com.orace.cra.domain.model.repository.UserRepository;
import com.orace.cra.execptions.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CraService {

    private final CraRepository craRepository;
    private final UserRepository userRepository;
    private final AssignmentRepository assignmentRepository;


    public CraResponse getMyCra(Long userId, int mois, int annee) {
        Cra cra = craRepository.findByCollaborateurIdAndMoisAndAnnee(userId, mois, annee)
                .orElseGet(() -> creerCra(userId, mois, annee));
        return toResponse(cra);
    }

    public List<CraResponse> getMesCras(Long userId) {
        return craRepository.findByCollaborateurId(userId).stream()
                .map(this::toResponse)
                .toList();
    }



    public CraResponse remplirMois(Long craId, Long userId) {
        Cra cra = findCraEditable(craId, userId);

        YearMonth moisCourant = YearMonth.of(cra.getAnnee(), cra.getMois());

        List<CraDay> jours = new ArrayList<>();
        for (int jour = 1; jour <= moisCourant.lengthOfMonth(); jour++) {
            LocalDate date = moisCourant.atDay(jour);

            if (date.getDayOfWeek() == DayOfWeek.SATURDAY ||
                    date.getDayOfWeek() == DayOfWeek.SUNDAY) continue;

            CraDay craDay = new CraDay();
            craDay.setCra(cra);
            craDay.setDate(date);
            craDay.setType(DayType.TRAVAILLE);
            craDay.setValeur(1.0);
            jours.add(craDay);
        }

        cra.getJours().clear();
        cra.getJours().addAll(jours);
        return toResponse(craRepository.save(cra));
    }

    public CraResponse updateJour(Long craId, Long userId, RemplirJourRequest request) {
        Cra cra = findCraEditable(craId, userId);

        CraDay craDay = cra.getJours().stream()
                .filter(j -> j.getDate().equals(request.getDate()))
                .findFirst()
                .orElseGet(() -> {
                    CraDay nouveau = new CraDay();
                    nouveau.setCra(cra);
                    cra.getJours().add(nouveau);
                    return nouveau;
                });

        craDay.setDate(request.getDate());
        craDay.setType(request.getType());
        craDay.setValeur(request.getValeur());

        return toResponse(craRepository.save(cra));
    }


    public CraResponse soumettre(Long craId, Long userId) {
        Cra cra = findCraEditable(craId, userId);

        if (cra.getJours().isEmpty()) {
            throw new BusinessException("Vous ne pouvez pas soumettre un CRA vide");
        }

        cra.setStatut(CraStatus.SUBMITTED);
        return toResponse(craRepository.save(cra));
    }


    public List<CraResponse> getCrasAValider() {
        return craRepository.findByStatutIn(List.of(CraStatus.SUBMITTED))
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<CraResponse> getAllCras() {
        return craRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }


    public CraResponse approuver(Long craId) {
        Cra cra = findCraParStatut(craId, CraStatus.SUBMITTED, "Seul un CRA soumis peut être validé");
        cra.setStatut(CraStatus.APPROVED);
        return toResponse(craRepository.save(cra));
    }


    public CraResponse rejeter(Long craId, CraValidationRequest request) {
        if (request.getMotif() == null || request.getMotif().isBlank()) {
            throw new BusinessException("Le motif est obligatoire pour rejeter un CRA");
        }
        Cra cra = findCraParStatut(craId, CraStatus.SUBMITTED, "Seul un CRA soumis peut être rejeté");
        cra.setStatut(CraStatus.REJECTED);
        cra.setMotifRejet(request.getMotif());
        return toResponse(craRepository.save(cra));
    }


    public CraResponse invalider(Long craId, CraValidationRequest request) {
        if (request.getMotif() == null || request.getMotif().isBlank()) {
            throw new BusinessException("Le motif est obligatoire pour invalider un CRA");
        }
        Cra cra = findCraParStatut(craId, CraStatus.APPROVED, "Seul un CRA approuvé peut être invalidé");
        cra.setStatut(CraStatus.INVALIDATED);
        cra.setMotifRejet(request.getMotif());
        return toResponse(craRepository.save(cra));
    }


    private Cra creerCra(Long userId, int mois, int annee) {
        User collaborateur = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("Collaborateur introuvable"));

        Cra cra = new Cra();
        cra.setCollaborateur(collaborateur);
        cra.setMois(mois);
        cra.setAnnee(annee);
        cra.setStatut(CraStatus.DRAFT);
        return craRepository.save(cra);
    }

    private Cra findCraEditable(Long craId, Long userId) {
        Cra cra = craRepository.findById(craId)
                .orElseThrow(() -> new BusinessException("CRA introuvable"));

        if (!cra.getCollaborateur().getId().equals(userId)) {
            throw new BusinessException("Ce CRA ne vous appartient pas");
        }


        if (cra.getStatut() == CraStatus.APPROVED) {
            throw new BusinessException("Un CRA validé ne peut pas être modifié");
        }


        if (cra.getStatut() == CraStatus.REJECTED) {
            return cra;
        }

        if (!estDansFenetreDeclaration()) {
            throw new BusinessException("La saisie n'est possible qu'entre le 22 et le 28 du mois");
        }

        return cra;
    }

    private Cra findCraParStatut(Long craId, CraStatus statutAttendu, String messageErreur) {
        Cra cra = craRepository.findById(craId)
                .orElseThrow(() -> new BusinessException("CRA introuvable"));
        if (cra.getStatut() != statutAttendu) {
            throw new BusinessException(messageErreur);
        }
        return cra;
    }

    private boolean estDansFenetreDeclaration() {
        int jour = LocalDate.now(ZoneId.of("Europe/Paris")).getDayOfMonth();
        return jour >= 03 && jour <= 28;
       // return true;
    }

    private CraResponse toResponse(Cra cra) {
        List<CraDayResponse> jours = cra.getJours().stream()
                .map(j -> CraDayResponse.builder()
                        .id(j.getId())
                        .date(j.getDate())
                        .type(j.getType())
                        .valeur(j.getValeur())
                        .build())
                .toList();

        return CraResponse.builder()
                .id(cra.getId())
                .mois(cra.getMois())
                .annee(cra.getAnnee())
                .statut(cra.getStatut())
                .motifRejet(cra.getMotifRejet())
                .collaborateurNom(cra.getCollaborateur().getNom())
                .collaborateurPrenom(cra.getCollaborateur().getPrenom())
                .jours(jours)
                .build();
    }

    public AbsenceSummaryResponse getMesAbsences(Long userId, int annee) {
        User collaborateur = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("Collaborateur introuvable"));

        return buildAbsenceSummary(collaborateur, annee);
    }

    public AbsenceSummaryResponse getAbsencesCollaborateur(Long collaborateurId, int annee) {
        User collaborateur = userRepository.findById(collaborateurId)
                .orElseThrow(() -> new BusinessException("Collaborateur introuvable"));

        return buildAbsenceSummary(collaborateur, annee);
    }

    private AbsenceSummaryResponse buildAbsenceSummary(User collaborateur, int annee) {
        List<Cra> cras = craRepository.findByCollaborateurIdAndAnnee(collaborateur.getId(), annee);

        List<AbsenceResponse> absences = cras.stream()
                .flatMap(cra -> cra.getJours().stream()
                        .filter(j -> j.getType() != DayType.TRAVAILLE)
                        .map(j -> AbsenceResponse.builder()
                                .craDayId(j.getId())
                                .date(j.getDate())
                                .type(j.getType())
                                .valeur(j.getValeur())
                                .mois(cra.getMois())
                                .annee(cra.getAnnee())
                                .build()))
                .sorted(Comparator.comparing(AbsenceResponse::getDate))
                .toList();

        Map<String, Double> totauxParType = absences.stream()
                .collect(Collectors.groupingBy(
                        a -> a.getType().name(),
                        Collectors.summingDouble(AbsenceResponse::getValeur)
                ));

        Double totalJours = absences.stream()
                .mapToDouble(AbsenceResponse::getValeur)
                .sum();

        return AbsenceSummaryResponse.builder()
                .collaborateurId(collaborateur.getId())
                .collaborateurNom(collaborateur.getNom())
                .collaborateurPrenom(collaborateur.getPrenom())
                .annee(annee)
                .absences(absences)
                .totauxParType(totauxParType)
                .totalJours(totalJours)
                .build();
    }
}