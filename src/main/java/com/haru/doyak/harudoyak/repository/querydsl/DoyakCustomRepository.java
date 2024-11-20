package com.haru.doyak.harudoyak.repository.querydsl;

public interface DoyakCustomRepository {

    Long findDoyakAllCount(Long shareDoyakId);

    Long deleteDoyakByMemberIdAndShareDoyakId(Long memberId, Long shareDoyakId);

    long deleteDoyakByShareDoyakId(Long shareDoyakId);

    boolean existsByMemberIdAndShareDoyakId(Long memberId, Long shareDoyakId);

}
