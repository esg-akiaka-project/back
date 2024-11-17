package com.haru.doyak.harudoyak.repository.querydsl.impl;

import com.haru.doyak.harudoyak.dto.comment.ReqCommentDTO;
import com.haru.doyak.harudoyak.dto.comment.ResCommentDTO;
import com.haru.doyak.harudoyak.entity.Comment;
import com.haru.doyak.harudoyak.repository.querydsl.CommentCustomRepository;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.haru.doyak.harudoyak.entity.QComment.comment;
import static com.haru.doyak.harudoyak.entity.QMember.member;

@Repository
@RequiredArgsConstructor
public class CommentCustomRepositoryImpl implements CommentCustomRepository {
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
     * 서로도약 댓글 목록에 쓰일 data select
     * */
    @Override
    public List<ResCommentDTO.ResCommentDetailDTO> findeCommentAll(Long shareDoyakId) {

        return jpaQueryFactory
                .select(Projections.bean(
                        ResCommentDTO.ResCommentDetailDTO.class,
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

}
