package com.orace.cra.domain.services;

import com.orace.cra.domain.model.dtos.request.AssignmentRequest;
import com.orace.cra.domain.model.dtos.request.MissionRequest;
import com.orace.cra.domain.model.dtos.response.MissionResponse;
import com.orace.cra.domain.model.entities.Assignment;
import com.orace.cra.domain.model.entities.Mission;
import com.orace.cra.domain.model.entities.User;
import com.orace.cra.domain.model.repository.AssignmentRepository;
import com.orace.cra.domain.model.repository.MissionRepository;
import com.orace.cra.domain.model.repository.UserRepository;
import com.orace.cra.execptions.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MissionService {

    private final MissionRepository missionRepository;
    private final UserRepository userRepository;
    private final AssignmentRepository assignmentRepository;

    public List<MissionResponse> getAll() {
        return missionRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public MissionResponse getById(Long id) {
        return toResponse(findMission(id));
    }

    public MissionResponse create(MissionRequest request) {
        Mission mission = new Mission();
        mission.setTitre(request.getTitre());
        mission.setDescription(request.getDescription());
        mission.setMotif(request.getMotif());
        mission.setDateDebut(request.getDateDebut());
        mission.setDateFin(request.getDateFin());
        mission.setTjm(request.getTjm());
        return toResponse(missionRepository.save(mission));
    }

    public MissionResponse update(Long id, MissionRequest request) {
        Mission mission = findMission(id);
        mission.setTitre(request.getTitre());
        mission.setDescription(request.getDescription());
        mission.setMotif(request.getMotif());
        mission.setDateDebut(request.getDateDebut());
        mission.setDateFin(request.getDateFin());
        mission.setTjm(request.getTjm());
        return toResponse(missionRepository.save(mission));
    }

    public void delete(Long id) {
        missionRepository.delete(findMission(id));
    }

    public void assign(AssignmentRequest request) {
        User collaborateur = userRepository.findById(request.getCollaborateurId())
                .orElseThrow(() -> new BusinessException("Collaborateur introuvable"));
        Mission mission = findMission(request.getMissionId());

        Assignment assignment = new Assignment();
        assignment.setCollaborateur(collaborateur);
        assignment.setMission(mission);
        assignment.setDateDebut(request.getDateDebut());
        assignment.setDateFin(request.getDateFin());
        assignmentRepository.save(assignment);
    }



    private Mission findMission(Long id) {
        return missionRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Mission introuvable"));
    }

    private MissionResponse toResponse(Mission m) {
        return MissionResponse.builder()
                .id(m.getId())
                .titre(m.getTitre())
                .description(m.getDescription())
                .motif(m.getMotif())
                .dateDebut(m.getDateDebut())
                .dateFin(m.getDateFin())
                .tjm(m.getTjm())
                .build();
    }
}
