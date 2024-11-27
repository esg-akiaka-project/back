package com.haru.doyak.harudoyak.dto.member;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class LevelDTO {

    private Long recentContinuity;  // 최근연속 작성일
    private Long maxContinuity;     // 최대연속일
    private Long point;             // 경험치
    private Long logCount;          // daily doyak count
    private Long shareDoyakCount;  // share doyak count
    private LocalDate firstDate;
    private LocalDate logLastDate;
}
