package com.haru.doyak.harudoyak.dto.member;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.JoinColumn;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@AllArgsConstructor
@Data
public class MemberResDTO {

    private String profileUrl;// 프로필 사진 url
    private String email;      // 이메일 주소
    private String nickname;   // 닉네임
    private String aiNickname; // 도약이별명
    private String goalName;   // 도약목표명
    private Boolean isVerified;   // 이메일인증 상태
    private String provider;// 소셜로그인제공자 local, kakao, google
}
