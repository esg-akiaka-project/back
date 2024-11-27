package com.haru.doyak.harudoyak.repository.querydsl;

import com.haru.doyak.harudoyak.dto.sharedoyak.ReqShareDoyakDTO;
import com.haru.doyak.harudoyak.dto.sharedoyak.ResShareDoyakDTO;
import com.haru.doyak.harudoyak.entity.ShareDoyak;

import java.util.List;
import java.util.Optional;

public interface ShareDoyakCustomRepository {

    Optional<List<ResShareDoyakDTO.ResMemberShareDoyakDYO>> findMemberShareDoyakAll(Long memberId);

    long shareDoyakDelete(Long memberId, Long shareDoyakId);

    Optional<ShareDoyak> findShaereDoyakByMemeberId(Long memeberId, Long shareDoyakId);

    Optional<List<ResShareDoyakDTO.ResShareDoyakDTOS>> findeAll();

    long shareContentUpdate(Long shareDoyakId, ReqShareDoyakDTO reqShareDoyakDTO);

}
