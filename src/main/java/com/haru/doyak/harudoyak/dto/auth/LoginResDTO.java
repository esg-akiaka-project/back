package com.haru.doyak.harudoyak.dto.auth;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.haru.doyak.harudoyak.dto.file.FileDTO;
import com.haru.doyak.harudoyak.dto.member.LevelDTO;
import com.haru.doyak.harudoyak.dto.member.MemberDTO;
import com.haru.doyak.harudoyak.entity.File;
import com.haru.doyak.harudoyak.entity.Level;
import com.haru.doyak.harudoyak.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class LoginResDTO {
    MemberDTO member;
    LevelDTO level;
    FileDTO file;
}
