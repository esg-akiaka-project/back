package com.haru.doyak.harudoyak.dto.log;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class EmotionDTO {

    private String emotion;
    private Long emotionCount;

}
