package com.haru.doyak.harudoyak.dto.auth;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.haru.doyak.harudoyak.entity.File;
import com.haru.doyak.harudoyak.entity.Level;
import com.haru.doyak.harudoyak.entity.Member;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class LoginResDTO {
    @JsonIgnoreProperties({"password", "claims", "doyaks"/*, "memberId"*/})// TODO: 응답시 memberId 제외시키기(주석풀기)
    Member member;
    Level level;
    File file;
}
