package com.haru.doyak.harudoyak.util;

import com.haru.doyak.harudoyak.dto.auth.jwt.JwtRecord;
import com.haru.doyak.harudoyak.entity.Member;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

@Component
public class JwtProvider {

    private final SecretKey key;

    @Value("${jwt.atk.expiration-hour}")
    private int atkExpirationHour;
    @Value("${jwt.rtk.expiration-hour}")
    private int rtkExpirationHour;

    public JwtProvider(@Value("${jwt.jwt-key}") String keyValue) {
        byte[] keyBytes = keyValue.getBytes(StandardCharsets.UTF_8);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateAccessToken(Map<String, Object> valueMap) {
        return Jwts.builder()
                .header()
                    .add("typ", "atk")
                .and()
                .subject((String)valueMap.get("role"))
                .claims(valueMap)
                .issuedAt(Date.from(ZonedDateTime.now().toInstant()))
                .expiration(Date.from(ZonedDateTime.now().plusHours(atkExpirationHour).toInstant()))
                .signWith(key)
                .compact();
    }

    public String generateRefreshToken() {
        return Jwts.builder()
                .header()
                .add("typ", "rtk")
                .and()
                .issuedAt(Date.from(ZonedDateTime.now().toInstant()))
                .expiration(Date.from(ZonedDateTime.now().plusHours(rtkExpirationHour).toInstant()))
                .signWith(key)
                .compact();
    }

    public JwtRecord getJwtRecord(Member member) {
        return new JwtRecord("Bearer",
                generateAccessToken(member.getClaims()),
                generateRefreshToken(),
                member.getMemberId());
    }

    public boolean validateToken(String token){
        token = token;
        Map<String, Object> claims = null;

        try{
            claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

        } catch  (MalformedJwtException malformedJwtException) {
            throw new RuntimeException(malformedJwtException.getMessage());
        } catch (ExpiredJwtException expiredJwtException) {
            throw new RuntimeException(expiredJwtException.getMessage());
        } catch (InvalidClaimException invalidClaimException) {
            throw new RuntimeException(invalidClaimException.getMessage());
        } catch (JwtException jwtException) {
            throw new RuntimeException(jwtException.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

        return true;
    }

    public Map<String, Object> extractClaimsFromJwt(String token) {
        Map<String, Object> claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims;
    }

    public String parseBearerToken(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(HttpHeaders.AUTHORIZATION))
                .filter(token -> token.length() > 7 && token.startsWith("Bearer "))
                .map(token -> token.substring(7))
                .orElse(null);
    }

}