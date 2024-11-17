package com.haru.doyak.harudoyak.repository.querydsl;

import com.haru.doyak.harudoyak.dto.sharedoyak.ReqShareDoyakDTO;
import com.haru.doyak.harudoyak.dto.sharedoyak.ResShareDoyakDTO;
import com.haru.doyak.harudoyak.entity.ShareDoyak;

import java.util.List;

public interface ShareDoyakCustomRepository {

    List<ResShareDoyakDTO> findMemberShareDoyakAll(Long memberId);

    long shareDoyakDelete(Long shareDoyakId);

    ShareDoyak findShaereDoyakByMemeberId(Long memeberId, Long shareDoyakId);

    List<ResShareDoyakDTO> findeAll();

    long shareContentUpdate(Long shareDoyakId, ReqShareDoyakDTO reqShareDoyakDTO);

}
