package com.haru.doyak.harudoyak.dto.log;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResLogDTO {
    // 도약기록 응답 정보를 받는 DTO

    private Long logId;        // 도약기록 아이디
    private LocalDateTime creationDate; // 도약기록 작성일
    private Long memberId;
    private String logContent;

}
