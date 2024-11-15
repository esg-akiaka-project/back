package com.haru.doyak.harudoyak.repository.querydsl;

public interface DoyakCustomRepository {

    Long findDoyakAllCount(Long shareDoyakId);

    Long deleteDoyak(Long memberId, Long shareDoyakId);

    boolean existsByMemberIdAndShareDoyakId(Long memberId, Long shareDoyakId);

}
