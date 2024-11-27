package com.haru.doyak.harudoyak.repository.querydsl.impl;

import com.haru.doyak.harudoyak.dto.sharedoyak.ReqShareDoyakDTO;
import com.haru.doyak.harudoyak.dto.sharedoyak.ResShareDoyakDTO;
import com.haru.doyak.harudoyak.entity.ShareDoyak;
import com.haru.doyak.harudoyak.repository.querydsl.ShareDoyakCustomRepository;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.haru.doyak.harudoyak.entity.QComment.comment;
import static com.haru.doyak.harudoyak.entity.QDoyak.doyak;
import static com.haru.doyak.harudoyak.entity.QFile.file;
import static com.haru.doyak.harudoyak.entity.QMember.member;
import static com.haru.doyak.harudoyak.entity.QShareDoyak.shareDoyak;

@Repository
@RequiredArgsConstructor
public class ShareDoyakCustomRepositoryImpl implements ShareDoyakCustomRepository {
    private final JPAQueryFactory jpaQueryFactory;

    /*
    * 회원 서로도약 목록 select
    * */
    @Override
    public Optional<List<ResShareDoyakDTO.ResMemberShareDoyakDYO>> findMemberShareDoyakAll(Long memberId){
        List<ResShareDoyakDTO.ResMemberShareDoyakDYO> resMemberShareDoyakDYOS = jpaQueryFactory
                .select(Projections.bean(
                        ResShareDoyakDTO.ResMemberShareDoyakDYO.class,
                        shareDoyak.shareDoyakId,
                        member.nickname.as("shareAuthorNickname")
                ))
                .from(shareDoyak)
                .leftJoin(member).on(shareDoyak.member.memberId.eq(member.memberId))
                .where(shareDoyak.member.memberId.eq(memberId))
                .fetch();
        return Optional.ofNullable(resMemberShareDoyakDYOS);
    }

    /*
    * 서로도약 delete
    * */
    @Override
    public long shareDoyakDelete(Long memberId, Long shareDoyakId) {
        return jpaQueryFactory
                .delete(shareDoyak)
                .where(shareDoyak.member.memberId.eq(memberId), shareDoyak.shareDoyakId.eq(shareDoyakId))
                .execute();
    }

    /*
    * 서로도약 작성한 회원 select
    * */
    @Override
    public Optional<ShareDoyak> findShaereDoyakByMemeberId(Long memeberId, Long shareDoyakId){

        return Optional.ofNullable(jpaQueryFactory
                .select(shareDoyak)
                .from(shareDoyak)
                .leftJoin(member).on(shareDoyak.member.memberId.eq(member.memberId))
                .where(shareDoyak.member.memberId.eq(memeberId), shareDoyak.shareDoyakId.eq(shareDoyakId))
                .fetchOne());
    }

    /*
    * 서로도약 content 수정
    * */
    @Override
    public long shareContentUpdate(Long shareDoyakId, ReqShareDoyakDTO reqShareDoyakDTO){
        return jpaQueryFactory
                .update(shareDoyak)
                .where(shareDoyak.shareDoyakId.eq(shareDoyakId))
                .set(shareDoyak.content, reqShareDoyakDTO.getShareContent())
                .execute();
    }

    /*
    * 서로도약 목록에 쓰일 data select
    * */
    @Override
    public Optional<List<ResShareDoyakDTO.ResShareDoyakDTOS>> findeAll() {

        // 서로도약의 데이터 목록을 select
        List<ResShareDoyakDTO.ResShareDoyakDTOS> resShareDoyakDTOS = jpaQueryFactory
                .select(Projections.bean(ResShareDoyakDTO.ResShareDoyakDTOS.class,
                                member.nickname.as("shareAuthorNickname"),
                                member.goalName,
                                shareDoyak.shareDoyakId,
                                shareDoyak.content.as("shareContent"),
                                file.filePathName.as("shareImageUrl"),
                                comment.commentId.countDistinct().as("commentCount"),
                                doyak.member.memberId.countDistinct().as("doyakCount")
                        )
                )
                .from(shareDoyak)
                .join(member).on(shareDoyak.member.memberId.eq(member.memberId))
                .join(file).on(shareDoyak.file.fileId.eq(file.fileId))
                .leftJoin(comment).on(comment.shareDoyak.shareDoyakId.eq(shareDoyak.shareDoyakId))
                .leftJoin(doyak).on(doyak.shareDoyak.shareDoyakId.eq(shareDoyak.shareDoyakId))
                .groupBy(shareDoyak.shareDoyakId)
                .orderBy(shareDoyak.creationDate.desc())
                .fetch();

        return Optional.ofNullable(resShareDoyakDTOS);

    }

}
