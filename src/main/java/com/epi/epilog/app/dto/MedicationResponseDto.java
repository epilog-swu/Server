package com.epi.epilog.app.dto;

import com.epi.epilog.app.domain.medication.MedicationStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
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

    @Data
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor(access =  AccessLevel.PRIVATE)
    public static class GetMedicationForm{
        private Long id;
        private Long nextId;
        private Long prevId;
        private String medicationName;
        private Boolean isAlarm;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
        private List<LocalTime> times;
        private LocalDate startDate;
        private LocalDate endDate;
        private List<String> weeks;
        private String effectiveness;
        private String precautions;
        private String storageMethod;
    }
}
