package com.haru.doyak.harudoyak.dto.log;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Data
@Getter
@Setter
public class TagWeeklyDTO {

    private LocalDateTime monday;
    private LocalDateTime sunday;
    private String tagName;
    private Long rn;

    @Getter
    @Setter
    public static class TagMontlyDTO {
        /*private LocalDateTime montly;*/
        private String tagName;
    }

}