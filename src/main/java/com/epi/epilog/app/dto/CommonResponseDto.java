package com.epi.epilog.app.dto;


import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class CommonResponseDto {
    @Data
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    static public class SuccessResponse {
        public boolean success;
    }

    @Data
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    static public class CommonResponse {
        public String message;
        public boolean success;
    }

    @Data
    @Getter
    static public class LocationRequest {
        public Double latitude;
        public Double longitude;
    }
}
