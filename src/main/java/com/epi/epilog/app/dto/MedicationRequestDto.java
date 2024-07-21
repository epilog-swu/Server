package com.epi.epilog.app.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class MedicationRequestDto {
    public static class MedicationSignUpDto {
        public String name;
        public List<LocalTime> times = new ArrayList<>();
    }

    @Data
    @Getter
    public static class MedicationAddedForm {
        private String medicationName;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
        private List<LocalTime> times;
        private LocalDate startDate;
        private LocalDate endDate;
        private Boolean endless;
        private Boolean isAlarm;
        private List<String> weeks;
        private String effectiveness;
        private String precautions;
        private String storageMethod;
    }
}
