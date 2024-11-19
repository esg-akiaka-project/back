package com.haru.doyak.harudoyak.repository.querydsl;

import com.haru.doyak.harudoyak.dto.log.EmotionDTO;
import com.haru.doyak.harudoyak.dto.log.ResLetterDTO;
import com.haru.doyak.harudoyak.dto.log.ResLogDTO;
import com.haru.doyak.harudoyak.dto.log.ResTagDTO;
import com.querydsl.core.Tuple;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface LogCustomRepository {

    /*
    * 월간 도약기록 상세 조회
    * */
    List<ResLetterDTO.LetterMonthlyDTO> findMontlyLetterAll(Long memberId, LocalDateTime startMonthDayDate, LocalDateTime endMonthDayDate);
    List<EmotionDTO.ResEmotionMonthlyDTO> findMontlyEmotion(Long memberId, LocalDateTime startMonthDayDate, LocalDateTime endMonthDayDate);
    List<ResTagDTO.TagMonthlyDTO> findMontlyTagAll(Long memberId, LocalDateTime startMonthDayDate, LocalDateTime endMonthDayDate);

    /*
    * 주간 도약기록 상세 조회
    * */
    List<ResLetterDTO.LetterWeeklyDTO> findLetterByDate(Long memberId, LocalDateTime mondayDate, LocalDateTime sundayDate);
    Optional<List<EmotionDTO>> findEmotionByDate(Long memberId, LocalDateTime mondayDate, LocalDateTime sundayDate);
    Optional<List<ResTagDTO.TagWeeklyDTO>> findTagsByName(Long memberId, LocalDateTime mondayDate, LocalDateTime sundayDate);

    /*
     * 일간 도약기록 상세 조회
     * */
    Optional<List<ResLogDTO.ResDailyLogDTO>> findLogByLogIdAndMemberId(Long memberId, Long logId);

    /*
     * 도약 기록 목록 조회
     * */
    Optional<List<ResLogDTO>> findLogAllByMemberId(Long memberId);

    List<Tuple> findLetterMemberWhereBetweenLogCreationDateTime(LocalDateTime startDate, LocalDateTime endDate);

    List<Tuple> findLogMemberWhereBetweenLogCreationDatetime(LocalDateTime startDate, LocalDateTime endDate);
}
