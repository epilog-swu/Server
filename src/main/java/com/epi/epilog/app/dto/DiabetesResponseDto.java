package com.epi.epilog.app.dto;

import com.epi.epilog.app.domain.enums.OccurrenceType;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

public class DiabetesResponseDto {

    /**
     * 오늘의 혈당 기록 리스트
     */
    @Data
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor(access =  AccessLevel.PRIVATE)
    static public class BloodSugarTodayResponse {
        public String date;
        public List<DiabetesBloodSugar> diabetes;
    }

    @Data
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor(access =  AccessLevel.PRIVATE)
    static public class DiabetesBloodSugar {
        public OccurrenceType occurrenceType;
        public Integer bloodSugar;
    }

    /**
     *
     */
}
