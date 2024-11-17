package com.haru.doyak.harudoyak.dto.log;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReqLogDTO {
    // 도약기록 요청 정보를 받는 DTO

    private Long logId;
    private Long memberId;
    private String logContent;    // 도약기록 내용
    private List<ResTagDTO> tagNameList; // 태그명 리스트
    private String emotion;    // 오늘의 감정
    private String logImageUrl; // 이미지파일 S3 url

}