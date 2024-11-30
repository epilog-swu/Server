package com.epi.epilog.app.dto;

import com.epi.epilog.app.domain.enums.MealType;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class MealsRequestDto {
    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class CreateMeal {
        MealType mealType;
        @JsonFormat(pattern = "HH:mm")
        LocalTime time;
        @JsonProperty("isAlarm")
        boolean isAlarm;
    }
}
