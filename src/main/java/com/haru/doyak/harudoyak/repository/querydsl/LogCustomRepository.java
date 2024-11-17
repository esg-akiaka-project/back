package com.haru.doyak.harudoyak.repository.querydsl;

import com.haru.doyak.harudoyak.dto.log.*;
import com.haru.doyak.harudoyak.dto.log.ResLogDTO;
import com.haru.doyak.harudoyak.dto.log.ResDailyLogDTO;
import com.querydsl.core.Tuple;

import java.time.LocalDateTime;
import java.util.List;

public interface LogCustomRepository {

    /*
    * 주간 도약기록 상세 조회
    * */
    List<LetterWeeklyDTO> findLetterByDate(Long memberId, LocalDateTime creationDate);
    List<EmotionDTO> findEmotionByDate(Long memberId, LocalDateTime creationDate);
    List<TagWeeklyDTO> findTagsByName(Long memberId, LocalDateTime creationDate);

    /*
     * 일간 도약기록 상세 조회
     * */
    List<ResDailyLogDTO> findLogByLogIdAndMemberId(Long memberId, Long logId);

    /*
     * 도약 기록 목록 조회
     * */
    List<ResLogDTO> findLogAllByMemberId(Long memberId);

    List<Tuple> findLetterMemberWhereBetweenLogCreationDateTime(LocalDateTime startDate, LocalDateTime endDate);

    List<Tuple> findLogMemberWhereBetweenLogCreationDatetime(LocalDateTime startDate, LocalDateTime endDate);
}
