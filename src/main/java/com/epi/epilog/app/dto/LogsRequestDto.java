package com.epi.epilog.app.dto;

import lombok.Data;
import lombok.Getter;

import java.util.List;

public class LogsRequestDto {
    /**
     * 일지 작성
     */
    @Getter
    @Data
    public static class LogCreateForm {
        private String date;
        private String occurenceType;
        private Double bloodSugar;
        private Double systolicBloodPressure;
        private Double diastolicBloodPressure;
        private Double heartRate;
        private Double weight;
        private Double bodyFatPercentage;
        private String bodyPhoto;
        private List<LogDetailCreateForm> exercise;
        private List<LogDetailCreateForm> mood;
    }

    @Getter
    @Data
    public static class LogDetailCreateForm {
        private String type;
        private String details;
    }
}
