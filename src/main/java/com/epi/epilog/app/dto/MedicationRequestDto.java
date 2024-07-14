package com.epi.epilog.app.dto;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class MedicationRequestDto {
    public static class MedicationSignUpDto {
        public String name;
        public List<LocalTime> times = new ArrayList<>();
    }
}
