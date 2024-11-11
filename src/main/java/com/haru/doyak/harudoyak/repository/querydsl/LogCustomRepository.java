package com.haru.doyak.harudoyak.repository.querydsl;

import com.haru.doyak.harudoyak.dto.log.ResLogDTO;
import com.querydsl.core.Tuple;

import java.time.LocalDateTime;
import java.util.List;

public interface LogCustomRepository {

    /*
     * 도약 기록 목록 조회
     * */
    List<ResLogDTO> findLogAllByMemberId(Long memberId);

    List<Tuple> findLogLetterMemberWhereArrivedDateBetweenDatetime(LocalDateTime startDate, LocalDateTime endDate);

    List<Tuple> findLogMemberWhereCreationDateBetweenDatetime(LocalDateTime startDate, LocalDateTime endDate);
}
