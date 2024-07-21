package com.epi.epilog.global.config;

import com.epi.epilog.global.exception.ApiException;
import com.epi.epilog.global.exception.ErrorCode;
import com.epi.epilog.global.utils.CustomUserDetails;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

public class CustomAuthenticationFilter extends OncePerRequestFilter {
    private static final String[] AUTH_WHITELIST = {
            "/api/auth/**",
//            "/api/diabetes/**",
            "/test"
    };

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String requestURI = request.getRequestURI();

        // Whitelist 엔드포인트에 대해 필터링을 건너뜁니다.
        if (Arrays.stream(AUTH_WHITELIST).anyMatch(requestURI::startsWith)) {
            filterChain.doFilter(request, response);
            return;
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails)){
            throw new ApiException(ErrorCode.INVALID_TOKEN);
        }
        filterChain.doFilter(request, response);
    }
}
