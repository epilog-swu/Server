package com.epi.epilog.app.dto;

import com.epi.epilog.app.domain.enums.MealStatus;
import com.epi.epilog.app.domain.enums.MedicationStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.List;


public class MealsResponseDto {
    @Data
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor(access =  AccessLevel.PRIVATE)
    public static class ChecklistDto {
        public LocalDate date;
        public List<ChecklistStateDto> checklist;
    }

    @Data
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor(access =  AccessLevel.PRIVATE)
    public static class ChecklistStateDto{
        public Long id;
        public String goalTime;
        public String title;
        public Boolean isComplete;
        public String state;
    }

    @Data
    @Getter
    public static class MealChecklistUpdateDto{
        @NotNull
        public String time;
        @NotNull
        public MealStatus status;
    }
}
