package com.haru.doyak.harudoyak.dto.sharedoyak;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class ResDoyakDTO {
    // 도약 응답 정보를 담는 DTO

    private Long doyakCount;// 해당 게시글의 총 도약수

}
