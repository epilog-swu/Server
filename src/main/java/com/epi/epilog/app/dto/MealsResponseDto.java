package com.epi.epilog.app.dto;

import com.epi.epilog.app.domain.enums.MealStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

public class MealsResponseDto {
    @Data
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class ChecklistDto {
        private Long id;
        private LocalDate date;
        private List<ChecklistStateDto> checklist;
    }

    @Data
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class ChecklistStateDto {
        private Long id;
        private String goalTime;
        private String title;
        private boolean isComplete;
        private String state;
    }

    @Data
    @Getter
    public static class MealChecklistUpdateDto {
        @NotNull
        private String time;
        @NotNull
        private MealStatus status;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class MealTimesDto {
        private Long id;
        @JsonProperty("title")
        private String title;
        @JsonProperty("isAlarm")
        private boolean isAlarm;

        @JsonIgnore
        private boolean isAlarm() {
            return isAlarm;
        }
    }
}
