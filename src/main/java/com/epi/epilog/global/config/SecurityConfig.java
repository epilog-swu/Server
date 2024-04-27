package com.epi.epilog.global.config;

import com.epi.epilog.global.utils.CustomUserDetailService;
import com.epi.epilog.global.utils.JwtUtil;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
@AllArgsConstructor
public class SecurityConfig {
    private final JwtUtil jwtUtil;
    private final CustomUserDetailService customUserDetailService;
    private static final String[] AUTH_WHITELIST = {
            "/api/auth/**"
    };
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // csrf, cors
        http.csrf(csrf -> csrf.disable());
        http.cors(Customizer.withDefaults());
        // session 비활성화
        http.sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(
                SessionCreationPolicy.STATELESS
                ));
        // form, basic http 비활성화
        http.formLogin((form)->form.disable());
        http.httpBasic(AbstractHttpConfigurer::disable);

        // usernamePasswordAuthenticationToken 앞에 jwt 필터체인 추가
        http.addFilterBefore(new JwtAuthFilter(jwtUtil, customUserDetailService), UsernamePasswordAuthenticationFilter.class);

        // 권한 규칙 설정
        http.authorizeHttpRequests(authorize -> authorize
                .requestMatchers(AUTH_WHITELIST).permitAll()
                .anyRequest().permitAll()
        );
        return http.build();
    }
}
