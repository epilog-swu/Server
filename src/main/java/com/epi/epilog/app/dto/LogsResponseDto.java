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
        private Long average;
        private Long preAverage;
        private Long postAverage;
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
        private Integer bloodSugar;
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
        private List<MonthWeightItem> day;
    }

    @Data
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor(access =  AccessLevel.PRIVATE)
    public static class MonthWeightItem {
        private String date;
        private Long weight;
        private Long bodyFatPercentage;
    }
}
