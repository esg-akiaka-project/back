package com.haru.doyak.harudoyak.repository.querydsl.impl;

import com.haru.doyak.harudoyak.dto.sharedoyak.*;
import com.haru.doyak.harudoyak.entity.Comment;
import com.haru.doyak.harudoyak.entity.ShareDoyak;
import com.haru.doyak.harudoyak.repository.querydsl.ShareDoyakCustomRepository;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

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
    * 회원 댓글 목록 select
    * */
    @Override
    public List<ResCommentDTO> findMemberCommentAll(Long memberId){
        List<ResCommentDTO> resCommentDTOS = jpaQueryFactory
                .select(Projections.bean(
                        ResCommentDTO.class,
                        comment.shareDoyak.shareDoyakId.as("commentShareDoyakId"),
                        comment.commentId,
                        member.memberId.as("commentAuthorId"),
                        member.nickname.as("commentAuthorNickname")
                ))
                .from(comment)
                .leftJoin(member).on(comment.member.memberId.eq(member.memberId))
                .where(comment.member.memberId.eq(memberId))
                .fetch();
        return resCommentDTOS;
    }

    /*
    * 회원 서로도약 목록 select
    * */
    @Override
    public List<ResShareDoyakDTO> findMemberShareDoyakAll(Long memberId){
        List<ResShareDoyakDTO> resShareDoyakDTOS = jpaQueryFactory
                .select(Projections.bean(
                        ResShareDoyakDTO.class,
                        shareDoyak.shareDoyakId,
                        member.nickname.as("shareAuthorNickname")
                ))
                .from(shareDoyak)
                .leftJoin(member).on(shareDoyak.member.memberId.eq(member.memberId))
                .where(shareDoyak.member.memberId.eq(memberId))
                .fetch();
        return resShareDoyakDTOS;
    }

    /*
    * 댓글 delete
    * */
    @Override
    public long commentDelete(Long commentId) {
        return jpaQueryFactory
                .delete(comment)
                .where(comment.commentId.eq(commentId))
                .execute();
    }

    /*
    * 서로도약 delete
    * */
    @Override
    public long shareDoyakDelete(Long shareDoyakId) {
        return jpaQueryFactory
                .delete(shareDoyak)
                .where(shareDoyak.shareDoyakId.eq(shareDoyakId))
                .execute();
    }

    /*
     * 댓글 내용 update
     * */
    @Override
    public long commentContentUpdate(Long commentId, ReqCommentDTO reqCommentDTO){
        return jpaQueryFactory
                .update(comment)
                .where(comment.commentId.eq(commentId))
                .set(comment.content, reqCommentDTO.getCommentContent())
                .execute();
    }

    /*
    * 댓글 작성한 회원 select
    * */
    @Override
    public Comment findCommentByMemberId(Long memberId, Long commentId){

        return jpaQueryFactory
                .select(comment)
                .from(comment)
                .leftJoin(member).on(comment.member.memberId.eq(member.memberId))
                .where(comment.member.memberId.eq(memberId), comment.commentId.eq(commentId))
                .fetchOne();
    }

    /*
    * 서로도약 작성한 회원 select
    * */
    @Override
    public ShareDoyak findShaereDoyakByMemeberId(Long memeberId, Long shareDoyakId){

        return jpaQueryFactory
                .select(shareDoyak)
                .from(shareDoyak)
                .leftJoin(member).on(shareDoyak.member.memberId.eq(member.memberId))
                .where(shareDoyak.member.memberId.eq(memeberId), shareDoyak.shareDoyakId.eq(shareDoyakId))
                .fetchOne();
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
    * 서로도약 댓글 목록에 쓰일 data select
    * */
    @Override
    public List<ResReplyCommentDTO> findeCommentAll(Long shareDoyakId) {

        return jpaQueryFactory
                .select(Projections.bean(
                        ResReplyCommentDTO.class,
                        comment.shareDoyak.shareDoyakId.as("commentShareDoyakId"),
                        comment.commentId,
                        comment.content.as("commentContent"),
                        member.nickname.as("commentAuthorNickname"),

                        // 서브쿼리
                        ExpressionUtils.as(
                                JPAExpressions
                                        .select(comment.count())
                                        .from(comment)
                                        .where(comment.parentCommentId.eq(comment.commentId)),
                                "replyCommentCount")

                        // 좀 더 공부해야 함
                       /* ExpressionUtils.as(
                                JPAExpressions
                                        .select(comment.commentId)
                                        .from(comment)
                                        .where(comment.parentCommentId.eq(comment.commentId))
                                        .fetch()
                                        .stream()
                                        .collect(Collectors.toList()),

                        "replyCommentIds")*/

                ))
                .from(comment)
                .leftJoin(member).on(comment.member.memberId.eq(member.memberId))
                .where(comment.parentCommentId.isNull(), comment.shareDoyak.shareDoyakId.eq(shareDoyakId))
                .orderBy(comment.creationDate.desc())
                .fetch();

    }

    /*
    * 서로도약 목록에 쓰일 data select
    * */
    @Override
    public List<ResShareDoyakDTO> findeAll() {

        // 서로도약의 데이터 목록을 select
        List<ResShareDoyakDTO> resShareDoyakDTOS = jpaQueryFactory
                .select(Projections.bean(ResShareDoyakDTO.class,
                                member.nickname.as("shareAuthorNickname"),
                                member.goalName,
                                shareDoyak.shareDoyakId,
                                shareDoyak.content.as("shareContent"),
                                file.filePathName.as("shareImageUrl"),
                                comment.commentId.countDistinct().as("commentCount"),
                                doyak.shareDoyak.shareDoyakId.countDistinct().as("doyakCount")
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

        // 댓글의 데이터 목록을 select
        /*List<ResCommentDTO> resCommentDTOS = jpaQueryFactory
                .select(Projections.bean(ResCommentDTO.class,
                        comment.shareDoyak.shareDoyakId.as("commentShareDoyakId"),
                        comment.commentId,
                        comment.content.as("commentContent"),
                        comment.member.nickname.as("commentAuthorNickname")
                ))
                .from(comment)
                .join(member).on(comment.member.memberId.eq(member.memberId))
                .fetch();*/

        return resShareDoyakDTOS;

        // 게시글 Id를 기준으로 댓글을 그룹화하여 매핑
        /*resShareDoyakDTOS.stream()
                        .peek(resShareDoyakDTO -> resShareDoyakDTO.setResComments(
                                resCommentDTOS.stream()
                                        .filter(comment -> comment.getCommentShareDoyakId().equals(resShareDoyakDTO.getShareDoyakId()))
                                        .collect(Collectors.toList())
                        ))
                        .collect(Collectors.toList());*/
    }
}
