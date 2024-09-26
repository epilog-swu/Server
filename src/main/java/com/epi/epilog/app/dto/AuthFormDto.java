package com.epi.epilog.app.dto;

import com.epi.epilog.app.domain.enums.GenderType;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

public class AuthFormDto {
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

    @Data
    @AllArgsConstructor
    @NoArgsConstructor(access=AccessLevel.PRIVATE)
    public static class SignupFormDto{
        private String loginId;
        private String password;
        private String name;
        private double stature;
        private double weight;
        private GenderType gender;
        private String protectorName;
        private String protectorPhone;
        private List<MedicationRequestDto.MedicationSignUpDto> medication;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor(access=AccessLevel.PRIVATE)
    @Builder
    @Getter
    public static class SignUpResponseDto {
        private boolean success;
        private String code;
        private String token;
    }
}
