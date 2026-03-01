package com.orace.cra.domain.model.dtos.request;


import com.orace.cra.domain.model.enums.DayType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;

@Data
public class RemplirJourRequest {
    @NotNull
    private LocalDate date;
    @NotNull
    private DayType type;
    @NotNull
    private Double valeur;
}


