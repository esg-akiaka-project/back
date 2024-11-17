package com.haru.doyak.harudoyak.dto.log;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResLogDTO {
    // 도약기록 응답 정보를 받는 DTO

    private Long logId;        // 도약기록 아이디
    private LocalDateTime creationDate; // 도약기록 작성일
    private Long memberId;
    private String logContent;

    /*
    * 일간 도약기록 응답 DTO
    * */
    @Setter
    @Getter
    public static class ResDailyLogDTO {

        private String emotion; // 오늘의 감정
        private String logContent; // 도약기록 내용
        private String logImageUrl; // 도약기록 파일url
        private List<ResTagDTO> tagNameList; // 태그명
        private String letterContent;             // 도약이 답변
        private LocalDateTime letterCreationDate; // 도약이 답변 생성일

    }

    /*
     * 주간 도약기록 응답 DTO
     * */
    @Getter
    @Setter
    public static class ResWeeklyLogDTO {


        private List<EmotionDTO> emotions;
        private List<ResTagDTO.TagWeeklyDTO> tags;
        private List<ResLetterDTO.LetterWeeklyDTO> aiFeedbacks;

    }

    /*
     * 월간 도약기록 응답 DTO
     * */
    @Getter
    @Setter
    public static class ResMontlyLogDTO {
        private List<EmotionDTO> emotions;
        private List<ResTagDTO.TagMontlyDTO> tags;
        private List<ResLetterDTO.LetterMontlyDTO> aiFeedbacks;
    }

}
