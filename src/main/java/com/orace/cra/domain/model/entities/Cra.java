package com.orace.cra.domain.model.entities;

import com.orace.cra.domain.model.enums.CraStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class Cra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User collaborateur;

    private int mois;
    private int annee;

    @Enumerated(EnumType.STRING)
    private CraStatus statut = CraStatus.DRAFT;

    private String motifRejet;

    @OneToMany(mappedBy = "cra", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CraDay> jours = new ArrayList<>();
}