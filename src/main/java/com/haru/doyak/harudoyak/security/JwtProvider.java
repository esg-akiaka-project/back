package com.haru.doyak.harudoyak.security;

import com.haru.doyak.harudoyak.dto.auth.jwt.JwtRecord;
import com.haru.doyak.harudoyak.entity.Member;
import com.haru.doyak.harudoyak.exception.CustomException;
import com.haru.doyak.harudoyak.exception.ErrorCode;
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
    @Value("${jwt.atk.typ}")
    private String atkTyp;
    @Value("${jwt.rtk.typ}")
    private String rtkType;

    public JwtProvider(@Value("${jwt.jwt-key}") String keyValue) {
        byte[] keyBytes = keyValue.getBytes(StandardCharsets.UTF_8);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateAccessToken(Map<String, Object> valueMap) {
        return Jwts.builder()
                .header()
                    .add("typ", atkTyp)
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
                .add("typ", rtkType)
                .and()
                .claims().add("site", "harudoyak")
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

    /**
     *
     * @param token
     * @param typ application-secret.yaml 에 지정된 토큰 타입(typ) 값 [access, refresh]
     * @return
     */
    public Map<String, Object> validateAndExtractClaims(String token, String typ){
        token = token;
        Map<String, Object> claims = null;

        try{
            Jws<Claims> jwsClaims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);

            String jwtType = jwsClaims.getHeader().getType();

            if(!jwtType.equals(typ)) throw new JwtException("토큰 타입이 일치하지 않음");

            claims = jwsClaims
                    .getPayload();

        } catch (ExpiredJwtException expiredJwtException) {
            throw new CustomException(ErrorCode.EXPIRED_JWT_TOKEN);
        } catch (MalformedJwtException | InvalidClaimException e) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        } catch (JwtException jwtException){
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.UNKNOWN_ERROR);
        }

        return claims;
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