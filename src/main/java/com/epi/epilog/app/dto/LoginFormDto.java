package com.epi.epilog.app.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;


public class LoginFormDto {
    @Data
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class PatientLoginFormDto {
        @NotNull(message = "아이디를 입력해주세요.")
        private String loginId;
        @NotNull(message="비밀번호를 입력해주세요.")
        private String password;
    }
    @Data
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class ProtectorLoginFormDto{
        @NotNull(message = "연동코드를 입력해주세요.")
        private String code;
    }
}
