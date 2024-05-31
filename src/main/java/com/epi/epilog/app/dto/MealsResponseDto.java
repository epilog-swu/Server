package com.epi.epilog.app.dto;

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
        public String goalTime;
        public String title;
        public String state;
    }
}
