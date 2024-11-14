package com.haru.doyak.harudoyak.security;

import lombok.Getter;

import java.util.Map;

@Getter
public class AuthenticatedUser {
    private final Long memberId;
    private final String role;
    private final String email;
    private final String aiNickname;

    public AuthenticatedUser(Map<String, Object> claims){
        this.memberId = Long.valueOf( String.valueOf( claims.get("memberId" )));
        this.role = (String)claims.get("role");
        this.email = (String)claims.get("email");
        this.aiNickname = (String)claims.get("aiNickname");
    }

    @Override
    public String toString() {
        return "AuthenticatedUser{" +
                "memberId=" + memberId +
                ", role='" + role + '\'' +
                ", email='" + email + '\'' +
                ", aiNickname='" + aiNickname + '\'' +
                '}';
    }
}
