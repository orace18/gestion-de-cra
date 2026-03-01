package com.orace.cra.domain.model.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
public class Assignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User collaborateur;

    @ManyToOne
    @JoinColumn(name = "mission_id")
    private Mission mission;

    private LocalDate dateDebut;
    private LocalDate dateFin;
}
