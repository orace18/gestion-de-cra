package com.orace.cra.domain.model.dtos.request;


import com.orace.cra.domain.model.enums.DayType;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class RemplirMoisRequest {
    private List<JourRequest> jours;

    @Data
    public static class JourRequest {
        private LocalDate date;
        private DayType type;
        private Double valeur;
    }
}