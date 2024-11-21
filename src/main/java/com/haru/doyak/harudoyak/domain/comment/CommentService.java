package com.haru.doyak.harudoyak.domain.comment;

import com.haru.doyak.harudoyak.domain.notification.NotificationService;
import com.haru.doyak.harudoyak.dto.notification.SseDataDTO;
import com.haru.doyak.harudoyak.domain.notification.SseEventName;
import com.haru.doyak.harudoyak.dto.comment.ReqCommentDTO;
import com.haru.doyak.harudoyak.dto.comment.ResCommentDTO;
import com.haru.doyak.harudoyak.entity.Comment;
import com.haru.doyak.harudoyak.entity.Member;
import com.haru.doyak.harudoyak.entity.ShareDoyak;
import com.haru.doyak.harudoyak.repository.MemberRepository;
import com.haru.doyak.harudoyak.repository.ShareDoyakRepository;
import com.haru.doyak.harudoyak.repository.querydsl.CommentCustomRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class CommentService {
    private final CommentCustomRepository commentCustomRepository;
    private final EntityManager entityManager;
    private final MemberRepository memberRepository;
    private final ShareDoyakRepository shareDoyakRepository;
    private final NotificationService notificationService;
    private final ConversionService conversionService;

    /*
     * 회원의 댓글 모아보기
     * @param : membaerId(Long)
     * */
    public List<ResCommentDTO> getMemberCommentList(Long memberId){
        List<ResCommentDTO> resCommentDTOS = commentCustomRepository.findMemberCommentAll(memberId);
        return resCommentDTOS;
    }

    /*
     * 댓글 수정
     * @param : memberId(Long), commentId(Long)
     * */
    @Transactional
    public long setCommentUpdate(Long memberId, Long commentId, ReqCommentDTO reqCommentDTO) {

        // 댓글의 작성자가 맞는지
        long commentAuthorId = 0;
        try{
            Comment selectComment = commentCustomRepository.findCommentByMemberId(memberId, commentId);
            commentAuthorId = selectComment.getMember().getMemberId();
        } catch (NullPointerException nullPointerException) {
            throw new NullPointerException("해당 댓글의 작성자가 아닙니다.");
        }

        // 댓글의 작성자가 맞다면
        long commentUpdateResult = 0;
        if(commentAuthorId == memberId){
            commentUpdateResult = commentCustomRepository.commentContentUpdate(commentId, reqCommentDTO);
            return commentUpdateResult;
        }

        // 아니라면
        return 0;
    }

    /*
     * 댓글 삭제
     * @param : memberId(Long), commentId(Long)
     * */
    @Transactional
    public long setCommentDelete(Long memberId, Long commentId){

        // 댓글의 작성자가 맞는지
        long commentAuthorId = 0;
        try{
            Comment selectComment = commentCustomRepository.findCommentByMemberId(memberId, commentId);
            commentAuthorId = selectComment.getMember().getMemberId();
        } catch (NullPointerException nullPointerException) {
            throw new NullPointerException("해당 댓글의 작성자가 아닙니다.");
        }

        // 댓글의 작성자가 맞다면
        long commentDeleteResult = 0;
        if(commentAuthorId == memberId){
            commentDeleteResult = commentCustomRepository.commentDelete(commentId);
            return commentDeleteResult;
        }

        // 아니라면
        return 0;

    }

    /*
     * 댓글 목록
     * @param : shareDoyakId(Long)
     * @return :
     * */
    public List<ResCommentDTO.ResCommentDetailDTO> getCommentList(Long shareDoyakId){
        List<ResCommentDTO.ResCommentDetailDTO> resReplyCommentDTOS = commentCustomRepository.findeCommentAll(shareDoyakId);
        return resReplyCommentDTOS;
    }

    /*
     * 댓글 작성
     * @param : memberId(Long), shareDoyakId(Long), commentContent(String)
     * @return :
     * */
    @Transactional
    public void setCommentAdd(Long memberId, Long shareDoyakId, ReqCommentDTO reqCommentDTO){
        // 회원아이디와 서로도약아이디 null체크
        if(memberId == 0 && shareDoyakId == 0){

        }

        /*// 회원 존재 여부 확인
        boolean isExistsMember = memberRepository.existsByMemberId(memberId);

        // 서로도약 존재 여부 확인
        boolean isExistsShareDoyak = shareDoyakRepository.existsById(shareDoyakId);*/

        // 회원 select
        Member selectMember = memberRepository.findMemberByMemberId(memberId).orElseThrow();

        // 서로도약 select
        ShareDoyak selectShareDoyak = shareDoyakRepository.findShareDoyakByShareDoyakId(shareDoyakId).orElseThrow();

        if(selectMember.getMemberId() != null && selectShareDoyak.getShareDoyakId() != null){

            if(reqCommentDTO.getParentCommentId() != null){

                // 대댓글(답글) insert
                Comment reply = Comment.builder()
                        .shareDoyak(selectShareDoyak)
                        .member(selectMember)
                        .content(reqCommentDTO.getCommentContent())
                        .parentCommentId(reqCommentDTO.getParentCommentId())
                        .build();
                entityManager.persist(reply);

                // 대댓글일떄 parent에 댓글 알림 전송
                Comment parent = commentCustomRepository.findCommentByCommentId(reply.getParentCommentId()).orElseThrow();
                SseDataDTO sseDataDTO = SseDataDTO.builder()
                        .sender(reply.getMember().getNickname())
                        .content(reply.getContent().substring(0, Math.min(15, reply.getContent().length())).concat("..."))
                        .postTitle(selectShareDoyak.getTitle())
                .build();
                notificationService.customNotify(parent.getMember().getMemberId(), sseDataDTO, "대댓글 알림", SseEventName.REPLY_COMMENT);
            }

            if(reqCommentDTO.getParentCommentId() == null){

                // 댓글 insert
                Comment comment = Comment.builder()
                        .shareDoyak(selectShareDoyak)
                        .member(selectMember)
                        .content(reqCommentDTO.getCommentContent())
                        .build();
                entityManager.persist(comment);

                // 게시글 작성자에게 댓글 알림 전송
                SseDataDTO sseDataDTO = SseDataDTO.builder()
                        .sender(selectShareDoyak.getMember().getNickname())
                        .content(comment.getContent().substring(0, Math.min(15, comment.getContent().length())).concat("..."))
                        .build();
                notificationService.customNotify(selectShareDoyak.getMember().getMemberId(), sseDataDTO, "서로도약 댓글 알림", SseEventName.POST_COMMENT);
            }

        }

    }

}
