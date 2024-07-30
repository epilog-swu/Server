package com.epi.epilog.app.dto;


import lombok.*;

import java.util.List;

@Getter
@Builder
@Data
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PdfData {
    private String date;
    private List<PdfLogDetail> logs;

    @Getter
    @Builder
    @Data
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class PdfLogDetail {
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

        private List<String> physicalActivity;
        private String physicalDetail;

        private List<String> mood;
        private String moodDetail;
    }
}
