package com.epi.epilog.app.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

public class FCMDto {
    @Data
    @Getter
    @Builder
    @NoArgsConstructor(access= AccessLevel.PRIVATE)
    @AllArgsConstructor(access=AccessLevel.PROTECTED)
    public static class FCMRequestForm {
        @NotNull
        private String token;
    }
}
