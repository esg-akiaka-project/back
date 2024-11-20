package com.haru.doyak.harudoyak.repository.querydsl.impl;

import com.haru.doyak.harudoyak.repository.querydsl.DoyakCustomRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import static com.haru.doyak.harudoyak.entity.QDoyak.doyak;

@Repository
@RequiredArgsConstructor
public class DoyakCustomRepositoryImpl implements DoyakCustomRepository {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public boolean existsByMemberIdAndShareDoyakId(Long memberId, Long shareDoyakId) {
        boolean existsByMemberId = jpaQueryFactory
                .selectFrom(doyak)
                .where(doyak.member.memberId.eq(memberId), doyak.shareDoyak.shareDoyakId.eq(shareDoyakId))
                .fetchFirst() != null;
        return existsByMemberId;
    }

    /*
     * 도약 삭제 : 해당 아이디의 도약수 삭제
     * req : memberId(Long)
     * */
    @Override
    public Long deleteDoyakByMemberIdAndShareDoyakId(Long memberId, Long shareDoyakId) {
        Long deleteDoyak = jpaQueryFactory
                .delete(doyak)
                .where(doyak.member.memberId.eq(memberId), doyak.shareDoyak.shareDoyakId.eq(shareDoyakId))
                .execute();
        return deleteDoyak ;
    }

    /*
    * 도약 삭제 : 서로도약 ID로 도약삭제
    * @param : shareDoyakId(Long)
    * */
    @Override
    public long deleteDoyakByShareDoyakId(Long shareDoyakId){
        long deleteDoyak = jpaQueryFactory
                .delete(doyak)
                .where(doyak.shareDoyak.shareDoyakId.eq(shareDoyakId))
                .execute();
        return deleteDoyak ;
    }

    /*
     * 해당 서로도약의 총 도약수
     * */
    @Override
    public Long findDoyakAllCount(Long shareDoyakId) {
        Long doyakCount = jpaQueryFactory
                .select(doyak.shareDoyak.shareDoyakId.count().as("doyakCount"))
                .from(doyak)
                .where(doyak.shareDoyak.shareDoyakId.eq(shareDoyakId))
                .fetchCount();
        return doyakCount;
    }
}
