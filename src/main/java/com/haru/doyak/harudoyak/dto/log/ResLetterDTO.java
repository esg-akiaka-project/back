package com.haru.doyak.harudoyak.dto.log;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Data
@Setter
@Getter
public class ResLetterDTO {
    // 도약이 편지 응답 정보를 받는 DTO

    private LocalDateTime date;
    private String feedback;

    /*
    * 주간 도약이 편지 응답 DTO
    * */
    @Setter
    @Getter
    public static class LetterWeeklyDTO {

        private LocalDateTime monday;
        private LocalDateTime sunday;
        private LocalDateTime feedBackDate;
        private String feedback;

    }

    /*
    * 월간 도약이 편지 응답 DTO
    * */
    @Getter
    @Setter
    public static class LetterMontlyDTO{
        /*private LocalDateTime montly;*/
        private Long aiFeedbackCount;
    }

}
