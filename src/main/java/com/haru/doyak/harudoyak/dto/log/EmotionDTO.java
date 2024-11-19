package com.haru.doyak.harudoyak.dto.log;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class EmotionDTO {

/*    private LocalDateTime monday;
    private LocalDateTime sunday;*/
    private String emotion;
    private Long emotionCount;

    @Getter
    @Setter
    public static class ResEmotionMonthlyDTO {

/*        private LocalDateTime startMonthDay;
        private LocalDateTime endMonthDay;*/
        private String emotion;
        private Long emotionCount;



    }

}
