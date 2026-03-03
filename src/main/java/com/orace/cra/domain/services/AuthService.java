package com.orace.cra.domain.services;

import com.orace.cra.config.JwtService;
import com.orace.cra.config.TokenBlacklistService;
import com.orace.cra.domain.model.dtos.response.ActionResponse;
import com.orace.cra.domain.model.dtos.response.AuthResponse;
import com.orace.cra.domain.model.dtos.request.LoginRequest;
import com.orace.cra.domain.model.entities.User;
import com.orace.cra.domain.model.repository.UserRepository;
import com.orace.cra.execptions.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final TokenBlacklistService tokenBlacklistService;

    public AuthResponse login(LoginRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessException("Utilisateur introuvable"));

        String token = jwtService.generateToken(user);

        return AuthResponse.builder()
                .token(token)
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }

    public ActionResponse logout(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new BusinessException("Token d'authentification manquant ou invalide");
        }

        String token = authHeader.substring(7);

        try {
            tokenBlacklistService.blacklist(token, jwtService.extractExpiration(token));
        } catch (RuntimeException ex) {
            throw new BusinessException("Token d'authentification invalide");
        }

        return ActionResponse.builder()
                .message("Déconnexion réussie")
                .build();
    }
}
