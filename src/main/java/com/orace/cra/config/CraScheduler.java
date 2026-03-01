package com.orace.cra.config;

import com.orace.cra.domain.model.entities.Cra;
import com.orace.cra.domain.model.entities.User;
import com.orace.cra.domain.model.enums.CraStatus;
import com.orace.cra.domain.model.enums.Role;
import com.orace.cra.domain.model.repository.AssignmentRepository;
import com.orace.cra.domain.model.repository.CraRepository;
import com.orace.cra.domain.model.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class CraScheduler {

    private final UserRepository userRepository;
    private final CraRepository craRepository;
    private final AssignmentRepository assignmentRepository;


    @Scheduled(cron = "0 0 6 1 * *", zone = "Europe/Paris")
    public void creerCrasMensuels() {
        LocalDate aujourdhui = LocalDate.now(ZoneId.of("Europe/Paris"));
        int mois = aujourdhui.getMonthValue();
        int annee = aujourdhui.getYear();

        log.info("Création automatique des CRA pour {}/{}", mois, annee);


        List<User> collaborateurs = userRepository.findAll().stream()
                .filter(u -> u.getRole() == Role.COLLABORATEUR && u.isActif())
                .toList();

        for (User collaborateur : collaborateurs) {

            boolean dejaExistant = craRepository
                    .findByCollaborateurIdAndMoisAndAnnee(collaborateur.getId(), mois, annee)
                    .isPresent();

            if (dejaExistant) continue;

            Cra cra = new Cra();
            cra.setCollaborateur(collaborateur);
            cra.setMois(mois);
            cra.setAnnee(annee);
            cra.setStatut(CraStatus.DRAFT);


            boolean enMission = assignmentRepository
                    .findActiveAssignment(collaborateur.getId(), aujourdhui)
                    .isPresent();

            if (!enMission) {
                log.info("{} {} : CRA créé en intercontrat",
                        collaborateur.getPrenom(), collaborateur.getNom());
            }

            craRepository.save(cra);
        }

        log.info("CRA créés pour {}/{}", mois, annee);
    }


    @Scheduled(cron = "0 0 18 28 * *", zone = "Europe/Paris")
    public void alerterCrasNonSoumis() {
        LocalDate aujourdhui = LocalDate.now(ZoneId.of("Europe/Paris"));
        int mois = aujourdhui.getMonthValue();
        int annee = aujourdhui.getYear();


        List<Cra> crasNonSoumis = craRepository.findByStatut(CraStatus.DRAFT).stream()
                .filter(cra -> cra.getMois() == mois && cra.getAnnee() == annee)
                .toList();

        if (crasNonSoumis.isEmpty()) {
            log.info("Tous les CRA de {}/{} ont été soumis", mois, annee);
            return;
        }

        log.warn(" ALERTE ADMIN — {} CRA non soumis au 28/{}/{} :",
                crasNonSoumis.size(), mois, annee);

        crasNonSoumis.forEach(cra ->
                log.warn(" {} {} ({})",
                        cra.getCollaborateur().getPrenom(),
                        cra.getCollaborateur().getNom(),
                        cra.getCollaborateur().getEmail())
        );
    }
}
