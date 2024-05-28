package com.epi.epilog.app.dto;

import com.epi.epilog.app.domain.annotations.ValidOccurenceType;
import com.epi.epilog.app.domain.enums.OccurrenceType;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;


public class DiabetesRequestDto {
    @Data
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    static public class BloodSugarRequest {
        public LocalDate date;
        @NotNull
        @ValidOccurenceType
        public String occurrenceType;
        @NotNull
        public Integer bloodSugar;
    }
}
