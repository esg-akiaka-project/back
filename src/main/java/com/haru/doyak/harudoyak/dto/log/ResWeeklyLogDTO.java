package com.haru.doyak.harudoyak.dto.log;

import lombok.*;

import java.util.List;

@Data
@Getter
@Setter
@NoArgsConstructor
public class ResWeeklyLogDTO {


    private List<EmotionDTO> emotions;
    private List<TagWeeklyDTO> tags;
    private List<LetterWeeklyDTO> aiFeedbacks;

    @Getter
    @Setter
    public static class ResMontlyLogDTO {
        private List<EmotionDTO> emotions;
        private List<TagWeeklyDTO.TagMontlyDTO> tags;
        private List<LetterWeeklyDTO.LetterMontlyDTO> aiFeedbacks;
    }

}
