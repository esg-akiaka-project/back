package com.haru.doyak.harudoyak.repository.querydsl;

import com.haru.doyak.harudoyak.dto.log.ResLogDTO;
import com.haru.doyak.harudoyak.dto.log.ResDailyLogDTO;

import java.util.List;

public interface LogCustomRepository {

    /*
     * 일간 도약기록 상세 조회
     * */
    List<ResDailyLogDTO> findLogByLogIdAndMemberId(Long memberId, Long logId);

    /*
     * 도약 기록 목록 조회
     * */
    List<ResLogDTO> findLogAllByMemberId(Long memberId);

}
