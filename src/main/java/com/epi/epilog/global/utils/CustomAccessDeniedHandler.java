package com.epi.epilog.global.utils;

import com.epi.epilog.global.exception.ErrorCode;
import com.epi.epilog.global.exception.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
@AllArgsConstructor
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    private final ObjectMapper objectMapper;
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .httpStatus(HttpStatus.FORBIDDEN)
                .code(ErrorCode.UNAUTHORIZED.getCode())
                .message(ErrorCode.UNAUTHORIZED.getMessage())
                .build();
        String responseBody = objectMapper.writeValueAsString(errorResponse);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(responseBody);
    }
}
