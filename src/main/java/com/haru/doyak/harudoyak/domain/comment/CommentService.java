package com.haru.doyak.harudoyak.domain.comment;

import com.haru.doyak.harudoyak.dto.comment.ReqCommentDTO;
import com.haru.doyak.harudoyak.dto.comment.ResCommentDTO;
import com.haru.doyak.harudoyak.entity.Comment;
import com.haru.doyak.harudoyak.entity.Member;
import com.haru.doyak.harudoyak.entity.ShareDoyak;
import com.haru.doyak.harudoyak.exception.CustomException;
import com.haru.doyak.harudoyak.exception.ErrorCode;
import com.haru.doyak.harudoyak.repository.MemberRepository;
import com.haru.doyak.harudoyak.repository.ShareDoyakRepository;
import com.haru.doyak.harudoyak.repository.querydsl.CommentRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class CommentService {
    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;
    private final ShareDoyakRepository shareDoyakRepository;

    /*
     * 회원의 댓글 모아보기
     * @param : membaerId(Long)
     * */
    public List<ResCommentDTO> getMemberCommentList(Long memberId){
        List<ResCommentDTO> resCommentDTOS = commentRepository.findMemberCommentAll(memberId);
        return resCommentDTOS;
    }

    /*
     * 댓글 수정
     * @param : memberId(Long), commentId(Long)
     * */

    /**
     * 댓글 수정
     * @param memberId
     * @param commentId
     * @param reqCommentDTO commentContent(댓글 내용)
     * @return commentUpdateResult 0(댓글 수정 실패), 1(댓글 수정 성공)
     * @throws EntityNotFoundException 해당 댓글이 존재하지 않을때
     * @throws  Exception 예기치 못한 시스템 에러
     */
    @Transactional
    public long setCommentUpdate(Long memberId, Long commentId, ReqCommentDTO reqCommentDTO) {

        try{
            // 댓글의 작성자가 맞는지
            Comment selectComment = commentRepository.findCommentByMemberId(memberId, commentId)
                                    .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));
            long commentAuthorId = selectComment.getMember().getMemberId();
            // 댓글의 작성자가 맞다면
            if(commentAuthorId == memberId){
                long commentUpdateResult = commentRepository.commentContentUpdate(commentId, reqCommentDTO);
                return commentUpdateResult;
            }else {
                throw new CustomException(ErrorCode.COMMENT_NOT_AUTHOR);
            }
        } catch (EntityNotFoundException entityNotFoundException) {
            throw new CustomException(ErrorCode.COMMENT_NOT_FOUND);
        } catch (Exception exception){
            throw new CustomException(ErrorCode.SYSTEM_CONNECTION_ERROR);
        }
    }

    /**
     * 댓글 삭제
     * @param memberId 회원pk
     * @param commentId 댓글pk
     * @return commentDeleteResult 0(댓글 삭제 실패), 1(댓글 삭제 성공)
     * @throws EntityNotFoundException 해당 댓글이 존재하지 않을때
     * @throws  Exception 예기치 못한 시스템 에러
     */
    @Transactional
    public long setCommentDelete(Long memberId, Long commentId){
        try{
            // 댓글의 작성자가 맞는지
            Comment selectComment = commentRepository.findCommentByMemberId(memberId, commentId)
                                    .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));
            long commentAuthorId = selectComment.getMember().getMemberId();
            // 댓글의 작성자가 맞다면
            if(commentAuthorId == memberId){
                long commentDeleteResult = commentRepository.commentDelete(commentId);
                return commentDeleteResult;
            }else {
                // 댓글의 작성자가 아니라면
                throw new CustomException(ErrorCode.COMMENT_NOT_AUTHOR);
            }

        } catch (EntityNotFoundException entityNotFoundException) {
            throw new CustomException(ErrorCode.COMMENT_NOT_FOUND);
        } catch (Exception exception){
            throw new CustomException(ErrorCode.SYSTEM_CONNECTION_ERROR);
        }
    }

    /**
     * 댓글 목록
     * @param shareDoyakId
     * @return resReplyCommentDTOS commentShareDoyakId(댓글의 서로도약fk), commentId(댓글pk), commentContent(댓글 내용), commentAuthorNickname(댓글 작성자 닉네임)
     *                             replyCommentCount(댓글의 답글 총 수), replies(댓글의 답글 목록)
     * @throws IllegalArgumentException 객체 필드와 select하는 컬럼명이 매핑이 안됐을 때
     * @throws NullPointerException select하는 값이 비어있을 때
     * @throws Exception 예기치 못한 시스템 에러
     */
    public List<ResCommentDTO.ResCommentDetailDTO> getCommentList(Long shareDoyakId){
        try {

            List<ResCommentDTO.ResCommentDetailDTO> resReplyCommentDTOS = commentRepository.findeCommentAll(shareDoyakId)
                                                                          .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_LIST_NOT_FOUND));
            return resReplyCommentDTOS;

        } catch (IllegalArgumentException illegalArgumentException){
            throw new CustomException(ErrorCode.SYNTAX_INVALID_FIELD);
        } catch (NullPointerException nullPointerException){
            throw new CustomException(ErrorCode.EMPTY_VALUE);
        } catch (Exception exception){
            throw new CustomException(ErrorCode.SYSTEM_CONNECTION_ERROR);
        }
    }

    /**
     * 댓글 작성
     * @param memberId 회원pk
     * @param shareDoyakId 서로도약pk
     * @param reqCommentDTO parentCommentId(댓글의 답글(대댓글) 부모pk), commentContent(댓글 내용)
     * @throws DataIntegrityViolationException 잘못 입력된 데이터가 왔을때
     * @throws Exception 예기치 못한 시스템 에러
     */
    @Transactional
    public void setCommentAdd(Long memberId, Long shareDoyakId, ReqCommentDTO reqCommentDTO){
        // 회원아이디와 서로도약아이디 null체크
        if(memberId == 0 && shareDoyakId == 0){

        }

        try {

            // 회원 select
            Member selectMember = memberRepository.findMemberByMemberId(memberId).orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

            // 서로도약 select
            ShareDoyak selectShareDoyak = shareDoyakRepository.findShareDoyakByShareDoyakId(shareDoyakId)
                                          .orElseThrow(() -> new CustomException(ErrorCode.SHARE_DOYAK_LIST_NOT_FOUND));

            // 가입한 회원이고 게시글이 있다면
            if(selectMember.getMemberId() != null && selectShareDoyak.getShareDoyakId() != null){

                // 파라미터로 parentCommentId가 있다면
                if(reqCommentDTO.getParentCommentId() != null){

                    // 대댓글(답글) insert
                    Comment reply = Comment.builder()
                            .shareDoyak(selectShareDoyak)
                            .member(selectMember)
                            .content(reqCommentDTO.getCommentContent())
                            .parentCommentId(reqCommentDTO.getParentCommentId())
                            .build();
                    commentRepository.save(reply);

                }

                // 파라미터로 parentCommentId가 없다면
                if(reqCommentDTO.getParentCommentId() == null){

                    // 댓글 insert
                    Comment comment = Comment.builder()
                            .shareDoyak(selectShareDoyak)
                            .member(selectMember)
                            .content(reqCommentDTO.getCommentContent())
                            .build();
                    commentRepository.save(comment);

                }

            }

        } catch (DataIntegrityViolationException dataIntegrityViolationException){
            throw new CustomException(ErrorCode.LETTER_INVALID_INPUT);
        } catch (Exception exception){
            throw new CustomException(ErrorCode.SYSTEM_CONNECTION_ERROR);
        }

    }

}
