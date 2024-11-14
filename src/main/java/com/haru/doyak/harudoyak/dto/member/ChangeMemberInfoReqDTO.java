package com.haru.doyak.harudoyak.dto.member;

import lombok.Data;

@Data
public class ChangeMemberInfoReqDTO {
    private String nickname;
    private String aiNickname;
    private String goalName;
    private String password;
    private String photoUrl;
}
