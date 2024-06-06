package com.epi.epilog.app.dto;

import lombok.*;

public class CommonResponseDto {
    @Data
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    static public class CommonResponse {
        public String message;
        public Boolean success;
    }

    @Data
    @Getter
    static public class LocationRequest {
        public Double latitude;
        public Double longitude;
    }
}
