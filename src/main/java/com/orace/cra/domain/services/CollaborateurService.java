package com.orace.cra.domain.services;

import com.orace.cra.domain.model.dtos.request.CollaborateurRequest;
import com.orace.cra.domain.model.dtos.response.CollaborateurResponse;
import com.orace.cra.domain.model.entities.User;
import com.orace.cra.domain.model.enums.Role;
import com.orace.cra.domain.model.repository.UserRepository;
import com.orace.cra.execptions.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CollaborateurService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public List<CollaborateurResponse> getAll() {
        return userRepository.findAll().stream()
                .filter(u -> u.getRole() == Role.COLLABORATEUR)
                .map(this::toResponse)
                .toList();
    }

    public CollaborateurResponse getById(Long id) {
        User user = findCollaborateur(id);
        return toResponse(user);
    }

    public CollaborateurResponse create(CollaborateurRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Email déjà utilisé");
        }
        if (request.getPassword() == null || request.getPassword().isBlank()) {
            throw new BusinessException("Le mot de passe du collaborateur est obligatoire");
        }
        User user = new User();
        user.setNom(request.getNom());
        user.setPrenom(request.getPrenom());
        user.setEmail(request.getEmail());
        user.setContrat(request.getContrat());
        user.setSeniorite(request.getSeniorite());
        user.setSalaire(request.getSalaire());
        user.setRole(Role.COLLABORATEUR);
        user.setActif(true);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        return toResponse(userRepository.save(user));
    }

    public CollaborateurResponse update(Long id, CollaborateurRequest request) {
        User user = findCollaborateur(id);
        user.setNom(request.getNom());
        user.setPrenom(request.getPrenom());
        user.setContrat(request.getContrat());
        user.setSeniorite(request.getSeniorite());
        user.setSalaire(request.getSalaire());
        return toResponse(userRepository.save(user));
    }

    public void toggleActivation(Long id) {
        User user = findCollaborateur(id);
        user.setActif(!user.isActif());
        userRepository.save(user);
    }



    private User findCollaborateur(Long id) {
        return userRepository.findById(id)
                .filter(u -> u.getRole() == Role.COLLABORATEUR)
                .orElseThrow(() -> new BusinessException("Collaborateur introuvable"));
    }

    private CollaborateurResponse toResponse(User user) {
        return CollaborateurResponse.builder()
                .id(user.getId())
                .nom(user.getNom())
                .prenom(user.getPrenom())
                .email(user.getEmail())
                .contrat(user.getContrat())
                .seniorite(user.getSeniorite())
                .salaire(user.getSalaire())
                .actif(user.isActif())
                .build();
    }
}

