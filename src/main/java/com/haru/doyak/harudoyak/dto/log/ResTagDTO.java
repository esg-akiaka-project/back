package com.haru.doyak.harudoyak.dto.log;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResTagDTO {
    // 태그 응답 정보를 받는 DTO

    private String tagName; // 태그 이름

    /*
     * 주간 태그 응답 DTO
     * */
    @Getter
    @Setter
    public static class TagWeeklyDTO {

/*        private LocalDateTime monday;
        private LocalDateTime sunday;*/
        private String tagName;
        private Long rn;

    }

    /*
    * 월간 태그 응답 DTO
    * */
    @Getter
    @Setter
    public static class TagMonthlyDTO {

/*        private LocalDateTime startMonthDay;
        private LocalDateTime endMonthDay;*/
        private String tagName;

    }

}
