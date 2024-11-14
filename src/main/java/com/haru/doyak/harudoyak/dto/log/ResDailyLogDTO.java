package com.haru.doyak.harudoyak.dto.log;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Setter
@Getter
public class ResDailyLogDTO {

    private Long logId;        // 도약기록 아이디
    private LocalDateTime logCreationDate; // 도약기록 작성일
    private String emotion; // 오늘의 감정
    private String logContent; // 도약기록 내용
    private String logImageUrl; // 도약기록 파일url
    private String aiNickname;                // 도약이 별명
    private String letterContent;             // 도약이 답변
    private LocalDateTime letterCreationDate; // 도약이 답변 생성일
    private List<TagDTO> tagNameList; // 태그명

}
