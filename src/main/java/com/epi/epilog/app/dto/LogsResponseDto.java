package com.epi.epilog.app.dto;

import lombok.*;
import java.util.List;

public class LogsResponseDto {
    /**
     * 월별 일지 개수 조회
     */
    @Data
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor(access =  AccessLevel.PRIVATE)
    public static class MonthLogsCount {
        private int year;
        private int month;
        private List<DayLogsCount> day;
    }

    @Data
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor(access =  AccessLevel.PRIVATE)
    public static class DayLogsCount {
        private String date;
        private int count;
    }

    /**
     * 일별 일지 목록 조회
     */
    @Data
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor(access =  AccessLevel.PRIVATE)
    public static class DayLogsList {
        private String date;
        private int count;
        private List<DayLogsItem> logs;
    }

    @Data
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor(access =  AccessLevel.PRIVATE)
    public static class DayLogsItem {
        private Long id;
        private String title;
        private List<String> keyword;
    }

    /**
     * 일별 평균, 식전후 평균 혈당 조회
     */
    @Data
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor(access =  AccessLevel.PRIVATE)
    public static class DayAvgBloodSugar {
        private String date;
        private double average;
        private double preAverage;
        private double postAverage;
    }

    /**
     * 일별 혈당 목록 조회
     */
    @Data
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor(access =  AccessLevel.PRIVATE)
    public static class DayBloodSugarList {
        private String date;
        private int count;
        private List<DayBloodSugarItem> bloodSugars;
    }

    @Data
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor(access =  AccessLevel.PRIVATE)
    public static class DayBloodSugarItem {
        private String title;
        private double bloodSugar;
    }

    /**
     * 월별 체중, 체지방률 목록 조회
     */
    @Data
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor(access =  AccessLevel.PRIVATE)
    public static class MonthWeightList {
        private int year;
        private int month;
        private List<MonthWeightItem> dayWeight;
        private List<MonthWeightItem> dayBodyFatPercentage;
    }

    @Data
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor(access =  AccessLevel.PRIVATE)
    public static class MonthWeightItem {
        private String date;
        private double value;
    }

    /**
     * 일지 상세 조회
     */
    @Data
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor(access =  AccessLevel.PRIVATE)
    public static class DetailAllLog {
        private String title;
        private List<String> keyword;
        private Double bloodSugar;
        private Double systolicBloodPressure;
        private Double diastolicBloodPressure;
        private Double heartRate;
        private Double weight;
        private Double bodyFatPercentage;
        private String bodyPhoto;
        private FallDetail fall;
        private LogDetail exercise;
        private LogDetail mood;
    }

    @Data
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor(access =  AccessLevel.PRIVATE)
    public static class FallDetail {
        private String address;
        private String mapImage;
    }

    @Data
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor(access =  AccessLevel.PRIVATE)
    public static class LogDetail {
        private String comment;
        private String details;
        private List<String> keyword;
    }
}
