package com.epi.epilog.app.dto;


import com.epi.epilog.app.domain.BaseEntity;
import lombok.*;

import java.util.List;

@Getter
@Builder
@Data
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PdfData extends BaseEntity {
    private String date;
    private List<PdfLogDetail> logs;
    private int entryCount;

    @Getter
    @Builder
    @Data
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class PdfLogDetail {
        private List<String> icons;
        private String time;
        private String location;
        private String mapImage;
        private double bloodSugar;

        private double systolic;
        private double diastolic;
        private double heartRate;

        private double weight;
        private double bodyFat;
        private String bodyImage;

        private LogsResponseDto.LogDetail physicalActivity;

        private LogsResponseDto.LogDetail mood;
    }
}
