package com.epi.epilog.global.utils;

import com.epi.epilog.app.dto.CustomUserInfoDto;
import com.epi.epilog.global.exception.ApiException;
import com.epi.epilog.global.exception.ErrorCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.sql.Date;
import java.time.ZonedDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtUtil {
    private final Key key;
    private final Long accessTokenExpirationTime;
    private final UserDetailsService userDetailsService;

    @Autowired
    public JwtUtil(@Value("${jwt.secret}") String secret,
                   @Value("${jwt.expiration_time}") Long accessTokenExpirationTime,
                   UserDetailsService userDetailsService) {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.accessTokenExpirationTime = accessTokenExpirationTime;
        this.userDetailsService = userDetailsService;
    }

    public String createAccessToken(CustomUserInfoDto member) {
        return createToken(member, accessTokenExpirationTime);
    }

    private String createToken(CustomUserInfoDto member, Long accessTokenExpirationTime) {
        Claims claims = Jwts.claims();
        claims.put("memberId", member.getId().toString());

        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime tokenValid = now.plusSeconds(accessTokenExpirationTime);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(Date.from(now.toInstant()))
                .setExpiration(Date.from(tokenValid.toInstant()))
                .signWith(this.key, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateJwt(String token) {
        try {
            if (token == null) {
                throw new ApiException(ErrorCode.INVALID_TOKEN);
            }
            Jwts.parserBuilder().setSigningKey(this.key).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SignatureException | MalformedJwtException e) {
            log.info("유효하지 않은 토큰", e);
        } catch (ExpiredJwtException e) {
            log.info("만료된 토큰", e);
        } catch (UnsupportedJwtException e) {
            log.info("지원하지 않는 토큰", e);
        } catch (IllegalArgumentException e) {
            log.info("JWT payload가 비어있음", e);
        }
        return false;
    }

    public String getUserById(String token) {
        return parseClaims(token).get("memberId", String.class); // 사용자 ID를 Long으로 반환
    }

    public Claims parseClaims(String token) {
        try {
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    public Authentication getAuthentication(String token) {
        Claims claims = parseClaims(token);
        Long memberId = claims.get("memberId", Long.class);

        UserDetails userDetails = userDetailsService.loadUserByUsername(memberId.toString());
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public String extractUsername(String token) {
        Claims claims = extractAllClaims(token);
        return claims.getSubject();
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(this.key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
