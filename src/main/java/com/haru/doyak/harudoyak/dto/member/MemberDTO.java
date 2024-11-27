package com.haru.doyak.harudoyak.dto.member;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class MemberDTO {
    private Long memberId;
    private String email;      // 이메일 주소
    private String nickname;   // 닉네임
    private String aiNickname; // 도약이별명
    private String goalName;   // 도약목표명
    private Boolean isVerified;   // 이메일인증 상태
    private String refreshToken;
}
