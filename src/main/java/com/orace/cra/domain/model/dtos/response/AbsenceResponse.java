package com.orace.cra.domain.model.dtos.response;


import com.orace.cra.domain.model.enums.DayType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class AbsenceResponse {
    private Long craDayId;
    private LocalDate date;
    private DayType type;
    private Double valeur;
    private int mois;
    private int annee;
}