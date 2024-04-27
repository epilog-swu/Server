package com.epi.epilog.global.config;

import com.epi.epilog.global.exception.ApiException;
import com.epi.epilog.global.exception.ErrorCode;
import com.epi.epilog.global.utils.CustomUserDetailService;
import com.epi.epilog.global.utils.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// OncePerRequestFilter -> 한 번만 실행하는 것을 보장하는 필터
@RequiredArgsConstructor
@Slf4j
public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final CustomUserDetailService customUserDetailService;

    /**
     * JWT 검증 절차 -> 필터로 수행
     * @param request
     * @param response
     * @param filterChain
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorizationHeader = request.getHeader("Authorization");
        log.info("jwt filter header="+authorizationHeader);
        // 헤더 유효성 검사
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);
            if (jwtUtil.validateJwt(token)) {
                // 사용자 정보 가져와서 UsernamePasswordAuthenticationToken 객체 생성
                Long userId = jwtUtil.getUserById(token);
                log.info("JwtAuthFilter userId=" + userId.toString());
                UserDetails userDetails = customUserDetailService.loadUserByUsername(userId.toString());
                log.info("JwtAuthFilter UserDetails="+userDetails);
                if (userDetails != null) {
                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                    // SecurityContextHolder에 추가
                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                }
            }
        } 
        filterChain.doFilter(request, response);
    }
}
