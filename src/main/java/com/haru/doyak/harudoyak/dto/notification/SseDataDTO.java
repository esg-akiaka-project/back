package com.haru.doyak.harudoyak.dto.notification;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@RequiredArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class SseDataDTO {
    private String sender;// 도약이는 도약이 이름, 사용자 닉네임
    private String postTitle;// 게시글 타이틀
    private Long shareDoyakId;// 이동
    private String startDate;// 이동 주간 월간 시작일
    private Long logId;// 이동 로그 조회 id
    private String content;// 자른 내용
    private Long count;// 기록 개수
    // 성장기록 - count
    // 편지 - sender, content
    // 댓글 - sender, postTitle, content
    // 대댓글 - sender, content
}
