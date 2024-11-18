package com.haru.doyak.harudoyak.repository.querydsl;

import com.haru.doyak.harudoyak.dto.log.*;
import com.haru.doyak.harudoyak.dto.log.ResLogDTO;
import com.querydsl.core.Tuple;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public interface LogCustomRepository {

    /*
    * 월간 도약기록 상세 조회
    * */
    List<ResLetterDTO.LetterMontlyDTO> findMontlyLetterAll(Long memberId, Date creationDate);
    List<EmotionDTO> findMontlyEmotion(Long memberId, Date creationDate);
    List<ResTagDTO.TagMontlyDTO> findMontlyTagAll(Long memberId, Date creationDate);

    /*
    * 주간 도약기록 상세 조회
    * */
    List<ResLetterDTO.LetterWeeklyDTO> findLetterByDate(Long memberId, Date creationDate);
    List<EmotionDTO> findEmotionByDate(Long memberId, Date creationDate);
    List<ResTagDTO.TagWeeklyDTO> findTagsByName(Long memberId, Date creationDate);

    /*
     * 일간 도약기록 상세 조회
     * */
    List<ResLogDTO.ResDailyLogDTO> findLogByLogIdAndMemberId(Long memberId, Long logId);

    /*
     * 도약 기록 목록 조회
     * */
    List<ResLogDTO> findLogAllByMemberId(Long memberId);

    List<Tuple> findLetterMemberWhereBetweenLogCreationDateTime(LocalDateTime startDate, LocalDateTime endDate);

    List<Tuple> findLogMemberWhereBetweenLogCreationDatetime(LocalDateTime startDate, LocalDateTime endDate);
}
