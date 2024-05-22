package com.epi.epilog.app.dto;

import com.epi.epilog.app.domain.annotations.ValidOccurenceType;
import com.epi.epilog.app.domain.enums.OccurrenceType;
import jakarta.validation.constraints.NotNull;
import lombok.*;


public class DiabetesRequestDto {
    @Data
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    static public class BloodSugarRequest {
        @NotNull
        @ValidOccurenceType
        public OccurrenceType occurrenceType;
        @NotNull
        public Integer bloodSugar;
    }
}
