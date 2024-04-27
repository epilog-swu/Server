package com.epi.epilog.global.utils;

import com.epi.epilog.app.dto.CustomUserInfoDto;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.sql.Date;
import java.time.ZonedDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtUtil {
    private final Key key;
    private final Long accessTokenExpirationTime;

    /**
     * 생성자에서 시크릿키 디코딩하여 jwt 필터에 저장
     * @param secret
     * @param accessTokenExpirationTime
     */
    @Autowired
    public JwtUtil(@Value("${jwt.secret}") String secret, @Value("${jwt.expiration_time}") Long accessTokenExpirationTime) {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.accessTokenExpirationTime = accessTokenExpirationTime;
    }

    /**
     * accessToken 생성
     * @param member
     * @return
     */
    public String createAccessToken(CustomUserInfoDto member){
        return createToken(member, accessTokenExpirationTime);
    }

    /**
     * JsonWebToken 생성
     * @param member
     * @param accessTokenExpirationTime
     */
    private String createToken(CustomUserInfoDto member, Long accessTokenExpirationTime){
        // Claims 생성 및 초기화
        Claims claims = Jwts.claims();
        claims.put("memberId", member.getMemberId());
        claims.put("loginId", member.getLoginId());
        claims.put("name", member.getName());
        claims.put("code", member.getCode());

        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime tokenValid = now.plusSeconds(accessTokenExpirationTime);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(Date.from(now.toInstant()))
                .setExpiration(Date.from(tokenValid.toInstant()))
                .signWith(this.key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * JWT 검증
     * @param token
     * @return
     */
    public boolean validateJwt(String token){
        try {
            Jwts.parserBuilder().setSigningKey(this.key).build().parseClaimsJwt(token);
            return true;
        } catch(io.jsonwebtoken.security.SignatureException | MalformedJwtException e) {
            log.info("유효하지 않은 토큰", e);
        } catch(ExpiredJwtException e){
            log.info("만료된 토큰", e);
        } catch (UnsupportedJwtException e){
            log.info("지원하지 않는 토큰", e);
        } catch (IllegalArgumentException e){
            log.info("JWT payload가 비어있음", e);
        }
        return false;
    }

    /**
     * JWT에서 멤버 ID 추출
     * @param token
     * @return
     */
    public Long getUserById(String token) {
        return parseClaims(token).get("memberId", Long.class);
    }

    /**
     * JWT payload 추출
     * @param token
     * @return
     */
    private Claims parseClaims(String token) {
        try{
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJwt(token).getBody();
        } catch(ExpiredJwtException e){
            return e.getClaims();
        }
    }

}
