package com.orace.cra.domain.model.dtos.response;

import com.orace.cra.domain.model.enums.DayType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class CraDayResponse {
    private Long id;
    private LocalDate date;
    private DayType type;
    private Double valeur;
}
