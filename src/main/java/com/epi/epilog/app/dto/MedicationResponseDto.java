package com.epi.epilog.app.dto;

import com.epi.epilog.app.domain.medication.MedicationStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

public class MedicationResponseDto {
    @Data
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor(access =  AccessLevel.PRIVATE)
    public static class ChecklistDto {
        private LocalDate date;
        private  Long medicationId;
        private List<MedicationResponseDto.ChecklistStateDto> checklist;
    }

    @Data
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor(access =  AccessLevel.PRIVATE)
    public static class ChecklistStateDto{
        private Long id;
        private String goalTime;
        private String title;
        private Boolean isComplete;
        private String state;
    }
    @Data
    @Getter
    public static class MedicineChecklistUpdateDto{
        @NotNull
        private String time;
        @NotNull
        private MedicationStatus status;
    }
}
