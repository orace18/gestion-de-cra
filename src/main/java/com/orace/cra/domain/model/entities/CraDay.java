package com.orace.cra.domain.model.entities;

import com.orace.cra.domain.model.enums.DayType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
public class CraDay {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cra_id")
    private Cra cra;

    private LocalDate date;

    @Enumerated(EnumType.STRING)
    private DayType type;

    private Double valeur;
}