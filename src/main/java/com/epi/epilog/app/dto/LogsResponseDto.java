package com.epi.epilog.app.dto;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

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
        private Integer year;
        private Integer month;
        private List<DayLogsCount> day;
    }

    @Data
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor(access =  AccessLevel.PRIVATE)
    public static class DayLogsCount {
        private String date;
        private Integer count;
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
        private Integer count;
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
        private Double average;
        private Double preAverage;
        private Double postAverage;
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
        private Integer count;
        private List<DayBloodSugarItem> bloodSugars;
    }

    @Data
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor(access =  AccessLevel.PRIVATE)
    public static class DayBloodSugarItem {
        private String title;
        private Double bloodSugar;
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
        private Integer year;
        private Integer month;
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
        private Double value;
    }
}
