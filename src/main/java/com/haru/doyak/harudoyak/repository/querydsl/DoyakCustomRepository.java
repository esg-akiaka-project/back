package com.haru.doyak.harudoyak.repository.querydsl;

import com.haru.doyak.harudoyak.entity.Doyak;

import java.util.List;
import java.util.Optional;

public interface DoyakCustomRepository {

    Optional<List<Doyak>> findDoyakAllByShareDoyakId(Long ShareDoyakId);

    Long findDoyakAllCount(Long shareDoyakId);

    Long deleteDoyakByMemberIdAndShareDoyakId(Long memberId, Long shareDoyakId);

    long deleteDoyakByShareDoyakId(Long shareDoyakId);

    boolean existsByMemberIdAndShareDoyakId(Long memberId, Long shareDoyakId);

}
