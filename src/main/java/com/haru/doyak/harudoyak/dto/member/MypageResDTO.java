package com.haru.doyak.harudoyak.dto.member;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.haru.doyak.harudoyak.entity.File;
import com.haru.doyak.harudoyak.entity.Level;
import com.haru.doyak.harudoyak.entity.Member;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MypageResDTO {
    Level level;
    File file;
}
