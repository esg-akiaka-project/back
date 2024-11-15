package com.haru.doyak.harudoyak.dto.log;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Data
@Setter
@Getter
public class LetterWeeklyDTO {

    private LocalDateTime monday;
    private LocalDateTime sunday;
    private LocalDateTime feedBackDate;
    private String feedback;

}
