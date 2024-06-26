package com.epi.epilog.global.exception;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.epi.epilog.global.exception.ErrorCode.INVALID_HTTP_METHOD;

@Slf4j
@RestControllerAdvice
public class ExceptionHandlerAdvice {

    //모든 에러 -> 하위 에러에서 못받을 때
    @ExceptionHandler(Exception.class)
    public ResponseEntity handleException(Exception e){
        // NestedExceptionUtils.getMostSpecificCause() -> 가장 구체적인 원인, 즉 가장 근본 원인을 찾아서 반환
        log.error("[Exception] cause: {} , message: {}", NestedExceptionUtils.getMostSpecificCause(e), e.getMessage());
        ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
        if (e.getMessage().equals("접근이 거부되었습니다.")){
            errorCode = ErrorCode.INVALID_TOKEN;
        }
        ErrorResponse errorResponse = ErrorResponse.of(errorCode.getHttpStatus(), errorCode.getCode(), errorCode.getMessage());
        return ResponseEntity.status(errorCode.getHttpStatus()).body(errorResponse);
    }

    @ExceptionHandler(ApiException.class)
    public ResponseEntity handleSystemException(ApiException e){
        log.error("[SystemException] cause: {}, message: {}",NestedExceptionUtils.getMostSpecificCause(e),e.getMessage());
        ErrorCode errorCode = e.getErrorCode();
        ErrorResponse errorResponse = ErrorResponse.of(errorCode.getHttpStatus(),errorCode.getCode(),  errorCode.getMessage());
        return ResponseEntity.status(errorCode.getHttpStatus()).body(errorResponse);
    }

    //메소드가 잘못되었거나 부적합한 인수를 전달했을 경우 -> 필수 파라미터 없을 때
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity handleIllegalArgumentException(IllegalArgumentException e){
        log.error("[IlleagalArgumentException] cause: {} , message: {}",NestedExceptionUtils.getMostSpecificCause(e),e.getMessage());
        ErrorCode errorCode = ErrorCode.ILLEGAL_ARGUMENT_ERROR;
        ErrorResponse errorResponse = ErrorResponse.of(errorCode.getHttpStatus(),errorCode.getCode(),
                String.format("%s %s", errorCode.getMessage(), NestedExceptionUtils.getMostSpecificCause(e).getMessage()));
        return ResponseEntity.status(errorCode.getHttpStatus()).body(errorResponse);
    }

    //@Valid 유효성 검사에서 예외가 발생했을 때 -> requestbody에 잘못 들어왔을 때
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity handleMethodArgumentNotValidException(MethodArgumentNotValidException e){
        log.error("[MethodArgumentNotValidException] cause: {}, message: {}",NestedExceptionUtils.getMostSpecificCause(e),e.getMessage());
        ErrorCode errorCode = ErrorCode.INVALID_ARGUMENT_ERROR;
        ErrorResponse errorResponse = ErrorResponse.of(errorCode.getHttpStatus(),
                errorCode.getCode(),
                errorCode.getMessage(),
                e.getBindingResult());
        return ResponseEntity.status(errorCode.getHttpStatus()).body(errorResponse);
    }

    //잘못된 포맷 요청 -> Json으로 안보내다던지
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity handleHttpMessageNotReadableException(HttpMessageNotReadableException e){
        log.error("[HttpMessageNotReadableException] cause: {}, message: {}",NestedExceptionUtils.getMostSpecificCause(e),e.getMessage());
        ErrorCode errorCode = ErrorCode.INVALID_FORMAT_ERROR;
        ErrorResponse errorResponse = ErrorResponse.of(errorCode.getHttpStatus(), errorCode.getCode(),  errorCode.getMessage());
        return ResponseEntity.status(errorCode.getHttpStatus()).body(errorResponse);
    }
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleHttpMethodException(
            HttpRequestMethodNotSupportedException e,
            HttpServletRequest request
    ) {
        log.error("[HttpRequestMethodNotSupportedException] " +
                        "url: {} | errorType: {} | errorMessage: {} | cause Exception: ",
                request.getRequestURL(), INVALID_HTTP_METHOD, INVALID_HTTP_METHOD.getMessage(), e);

        ErrorCode errorCode = INVALID_HTTP_METHOD;
        ErrorResponse errorResponse = ErrorResponse.of(errorCode.getHttpStatus(), errorCode.getCode(),  errorCode.getMessage());
        return ResponseEntity.status(errorCode.getHttpStatus()).body(errorResponse);
    }
    // JWT 관련 예외 처리
    @ExceptionHandler({
            io.jsonwebtoken.security.SignatureException.class,
            io.jsonwebtoken.MalformedJwtException.class,
            io.jsonwebtoken.ExpiredJwtException.class,
            io.jsonwebtoken.UnsupportedJwtException.class
    })
    public ResponseEntity<ErrorResponse> handleJwtException(Exception e) {
        log.error("[JwtException] cause: {}, message: {}", NestedExceptionUtils.getMostSpecificCause(e), e.getMessage());
        ErrorCode errorCode = ErrorCode.INVALID_TOKEN; // 여기에 적절한 에러 코드를 설정
        ErrorResponse errorResponse = ErrorResponse.of(errorCode.getHttpStatus(), errorCode.getCode(), errorCode.getMessage());
        return ResponseEntity.status(errorCode.getHttpStatus()).body(errorResponse);
    }
}