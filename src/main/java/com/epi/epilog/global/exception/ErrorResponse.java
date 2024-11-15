package com.epi.epilog.global.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.List;

@Builder
@Getter
@RequiredArgsConstructor
@Schema(description = "예외 처리 응답")
public class ErrorResponse {
    @Schema(description = "요청 성공 여부")
    private final boolean success = false;
    @Schema(description = "HTTP Status")
    private final HttpStatus httpStatus;
    @Schema(description = "Dialog Status")
    private final int code;
    @Schema(description = "설명")
    private final String message;

    public static ErrorResponse of(HttpStatus httpStatus, int code, String message) {
        return ErrorResponse.builder()
                .httpStatus(httpStatus)
                .code(code)
                .message(message)
                .build();
    }

    public static ErrorResponse of(HttpStatus httpStatus, int code, String message, BindingResult bindingResult) {
        return ErrorResponse.builder()
                .httpStatus(httpStatus)
                .code(code)
                .message(message)
                .build();
    }

    @Getter
    public static class ValidationError {
        private final String field;
        private final String message;

        private ValidationError(FieldError fieldError) {
            this.field = fieldError.getField();
            this.message = fieldError.getDefaultMessage();
        }

        public static List<ValidationError> of(final BindingResult bindingResult) {
            return bindingResult.getFieldErrors().stream()
                    .map(ValidationError::new)
                    .toList();
        }

    }
}
