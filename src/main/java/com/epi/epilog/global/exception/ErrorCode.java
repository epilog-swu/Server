package com.epi.epilog.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    // 1xxx
    INVALID_ARGUMENT_ERROR(HttpStatus.BAD_REQUEST, 400, "올바르지 않은 파라미터입니다."),
    INVALID_DATETIME_ERROR(HttpStatus.BAD_REQUEST, 400, "올바르지 않은 시간 포맷입니다."),
    INVALID_FORMAT_ERROR(HttpStatus.BAD_REQUEST,400, "올바르지 않은 포맷입니다."),
    INVALID_TYPE_ERROR(HttpStatus.BAD_REQUEST, 400, "올바르지 않은 타입입니다."),
    ILLEGAL_ARGUMENT_ERROR(HttpStatus.BAD_REQUEST, 400, "필수 파라미터가 없습니다"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 500, "서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요."),
    INVALID_HTTP_METHOD(HttpStatus.METHOD_NOT_ALLOWED, 400, "잘못된 Http Method 요청입니다."),

    LOGIN_FAILED(HttpStatus.UNAUTHORIZED, 401, "로그인에 실패했습니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, 401, "권한이 없습니다"),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, 401, "유효하지 않은 토큰입니다"),

    // 2xxx
    USER_NOT_FOUND(HttpStatus.BAD_REQUEST, 2000, "유저를 찾을 수 없습니다.");

    private final HttpStatus httpStatus;
    private final int code;
    private final String message;
}
