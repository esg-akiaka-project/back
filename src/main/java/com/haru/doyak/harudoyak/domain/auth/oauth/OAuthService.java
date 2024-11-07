package com.haru.doyak.harudoyak.domain.auth.oauth;

import com.haru.doyak.harudoyak.dto.auth.JwtMemberDTO;
import com.haru.doyak.harudoyak.dto.jwt.JwtRecord;
import com.haru.doyak.harudoyak.entity.Level;
import com.haru.doyak.harudoyak.entity.Member;
import com.haru.doyak.harudoyak.repository.LevelRepository;
import com.haru.doyak.harudoyak.repository.MemberRepository;
import com.haru.doyak.harudoyak.util.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OAuthService {
    private final WebClient webClient = WebClient.create();
    private final MemberRepository memberRepository;
    private final JwtProvider jwtProvider;
    private final LevelRepository levelRepository;

    @Value("${spring.oauth2.google.client-id}")
    private String google_client_id;
    @Value("${spring.oauth2.google.client-secret}")
    private String google_client_secret;
    @Value("${spring.oauth2.google.redirect-uri}")
    private String google_redirect_uri;
    @Value("${spring.oauth2.google.client-name}")
    private String google_client_name;
    @Value("${spring.oauth2.kakao.client-id}")
    private String kakao_client_id;
    @Value("${spring.oauth2.kakao.client-secret}")
    private String kakao_client_secret;
    @Value("${spring.oauth2.kakao.redirect-uri}")
    private String kakao_redirect_uri;
    @Value("${spring.oauth2.kakao.client-name}")
    private String kakao_client_name;


    public GoogleTokenResponse requestGoogleAccessToken(String authorizationCode) {
        Map<String, Object> params = new HashMap<>();
        params.put("client_id", google_client_id);
        params.put("client_secret", google_client_secret);
        params.put("redirect_uri", google_redirect_uri);
        params.put("code", authorizationCode);
        params.put("grant_type", "authorization_code");

        return webClient.post()
                .uri("https://oauth2.googleapis.com/token")
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(params)
                .retrieve()
                .onStatus(status -> status.is4xxClientError(), response ->
                        response.bodyToMono(String.class).flatMap(body -> {
                            throw new RuntimeException("Error response: " + body);
                        }))
                .bodyToMono(GoogleTokenResponse.class)
                .block();
    }

    public GoogleUserResponse requestGoogleUserInfo(GoogleTokenResponse googleTokenResponse){
        return webClient.get()
                .uri("https://www.googleapis.com/oauth2/v2/userinfo")
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer "+googleTokenResponse.getAccess_token())
                .retrieve()
                .onStatus(status -> status.is4xxClientError(), response ->
                        response.bodyToMono(String.class).flatMap(body -> {
                            throw new RuntimeException("Error response: " + body);
                        }))
                .bodyToMono(GoogleUserResponse.class)
                .block();
    }

    public JwtMemberDTO googleLogin(String authorizationCode){
        GoogleUserResponse userInfo = requestGoogleUserInfo(requestGoogleAccessToken(authorizationCode));
        // 이메일로 가입된 회원인지 확인하기
        String providerId = google_client_name+"_"+userInfo.getId();
        Optional<Member> optionalMember = memberRepository.findMemberByProviderId(providerId);
        Member savedMember;
        if(optionalMember.isEmpty()){
            // 가입 안되어있으면 가입시키기
            Member member = Member.builder()
                    .email(userInfo.email)
                    .isVerified(true)
                    .provider(google_client_name)
                    .providerId(providerId)
                    .nickname(userInfo.name)
                    .build();
            memberRepository.save(member);
            savedMember = member;
            // 레벨 생성하기
            Level level = Level.builder()
                    .member(savedMember)
                    .point(10L)// 가입시 10포인트
                    .build();
            levelRepository.save(level);
        }else {
            savedMember = optionalMember.get();
        }
        // 토큰 발행
        JwtRecord jwtRecord = jwtProvider.getJwtRecord(savedMember);
        savedMember.updateRefreshToken(jwtRecord.refreshToken());
        memberRepository.save(savedMember);
        return JwtMemberDTO.builder()
                .jwtRecord(jwtRecord)
                .member(savedMember)
                .build();
    }

    public JwtMemberDTO kakaoLogin(String authorizationCode) {
        KakaoUserResponse userInfo = requestKakaoUserInfo(requestKakaoAccessToken(authorizationCode));
        // 이메일로 가입된 회원인지 확인하기
        String providerId = kakao_client_name+"_"+userInfo.getId();
        Optional<Member> optionalMember = memberRepository.findMemberByKakaoId(providerId);
        Member savedMember;
        if(optionalMember.isEmpty()){
            // 가입 안되어있으면 가입시키기
            Member member = Member.builder()
                    .provider(kakao_client_name)
                    .providerId(providerId)
                    .isVerified(true)
                    .nickname(providerId)
                    .build();
            memberRepository.save(member);
            savedMember = member;
            // 레벨 생성하기
            Level level = Level.builder()
                    .member(savedMember)
                    .point(10L)// 가입시 10포인트
                    .build();
            levelRepository.save(level);
        }else {
            savedMember = optionalMember.get();
        }
        // 토큰 발행
        JwtRecord jwtRecord = jwtProvider.getJwtRecord(savedMember);
        savedMember.updateRefreshToken(jwtRecord.refreshToken());
        memberRepository.save(savedMember);
        return JwtMemberDTO.builder()
                .jwtRecord(jwtRecord)
                .member(savedMember)
                .build();
    }

    public KakaoTokenResponse requestKakaoAccessToken(String authorizationCode){
        return webClient.post()
                .uri("https://kauth.kakao.com/oauth/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData("client_id", kakao_client_id)
                        .with("client_secret", kakao_client_secret)
                        .with("redirect_uri", kakao_redirect_uri)
                        .with("code", authorizationCode)
                        .with("grant_type", "authorization_code"))
                .retrieve()
                .onStatus(status -> status.is4xxClientError(), response ->
                        response.bodyToMono(String.class).flatMap(body -> {
                            throw new RuntimeException("Error response: " + body);
                        }))
                .bodyToMono(KakaoTokenResponse.class)
                .block();
    }

    public KakaoUserResponse requestKakaoUserInfo(KakaoTokenResponse kakaoTokenResponse){
        return webClient.get()
                .uri("https://kapi.kakao.com/v2/user/me")
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer "+kakaoTokenResponse.getAccess_token())
                .retrieve()
                .onStatus(status -> status.is4xxClientError(), response ->
                        response.bodyToMono(String.class).flatMap(body -> {
                            throw new RuntimeException("Error response: " + body);
                        }))
                .bodyToMono(KakaoUserResponse.class)
                .block();
    }
}
