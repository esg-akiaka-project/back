package com.haru.doyak.harudoyak.domain.comment;

import com.haru.doyak.harudoyak.annotation.Authenticated;
import com.haru.doyak.harudoyak.dto.comment.ReqCommentDTO;
import com.haru.doyak.harudoyak.dto.comment.ResCommentDTO;
import com.haru.doyak.harudoyak.security.AuthenticatedUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("api/posts/comments/*")
@RestController
public class CommentController {
    private final CommentService commentService;

    /**
     * 댓글 삭제
     * @param memberId 회원pk
     * @param commentId 댓글pk
     * @return commentDeleteResult 0(댓글 삭제 실패), 1(댓글 삭제 성공)
     */
    @PutMapping("{memberId}/{commentId}")
    public ResponseEntity<String> setCommentUpdate(@Authenticated AuthenticatedUser authenticatedUser, @PathVariable("memberId") Long memberId, @PathVariable("commentId") Long commentId, @RequestBody ReqCommentDTO reqCommentDTO) {
        long commentUpdateResult = commentService.setCommentUpdate(authenticatedUser.getMemberId(), commentId, reqCommentDTO);
        if(commentUpdateResult == 1) {
            return ResponseEntity.ok("댓글 수정이 완료되었습니다.");
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * 댓글 삭제
     * @param memberId 회원pk
     * @param commentId 댓글pk
     * @return commentDeleteResult 0(댓글 삭제 실패), 1(댓글 삭제 성공)
     */
    @DeleteMapping("{memberId}/{commentId}")
    public ResponseEntity<String> setCommentDelete(@Authenticated AuthenticatedUser authenticatedUser, @PathVariable("memberId") Long memberId, @PathVariable("commentId") Long commentId){
        long commentDeleteResult = commentService.setCommentDelete(authenticatedUser.getMemberId(), commentId);
        if(commentDeleteResult == 1){
            return ResponseEntity.ok("댓글 삭제가 완료되었습니다.");
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * 댓글 목록
     * @param shareDoyakId
     * @return resReplyCommentDTOS commentShareDoyakId(댓글의 서로도약fk), commentId(댓글pk), commentContent(댓글 내용), commentAuthorNickname(댓글 작성자 닉네임)
     *                             replyCommentCount(댓글의 답글 총 수), replies(댓글의 답글 목록)
     */
    @GetMapping("list/{shareDoyakId}")
    public ResponseEntity<List<ResCommentDTO.ResCommentDetailDTO>> getCommentList(@PathVariable("shareDoyakId") Long shareDoyakId){
        List<ResCommentDTO.ResCommentDetailDTO> resReplyCommentDTOS = commentService.getCommentList(shareDoyakId);
        return ResponseEntity.ok(resReplyCommentDTOS);
    }

    /**
     * 대댓글 작성
     * @param memberId 회원pk
     * @param shareDoyakId 서로도약pk
     * @param reqCommentDTO parentCommentId(댓글의 답글(대댓글) 부모pk), commentContent(댓글 내용)
     */
    @PostMapping("replys/{memberId}/{shareDoyakId}/{commentId}")
    public ResponseEntity<String> setCommentChildAdd(@Authenticated AuthenticatedUser authenticatedUser, @PathVariable("memberId") Long memberId, @PathVariable("shareDoyakId") Long shareDoyakId, @PathVariable("commentId") Long commentId, @RequestBody ReqCommentDTO reqCommentDTO) {
        if(commentId != null){
            reqCommentDTO.setParentCommentId(commentId);
            commentService.setCommentAdd(authenticatedUser.getMemberId(), shareDoyakId, reqCommentDTO);
            return ResponseEntity.ok("대댓글 작성을 완료했습니다.");
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * 댓글 작성
     * @param memberId 회원pk
     * @param shareDoyakId 서로도약pk
     * @param reqCommentDTO commentContent(댓글 내용)
     */
    @PostMapping("{memberId}/{shareDoyakId}")
    public ResponseEntity<String> setCommentAdd(@Authenticated AuthenticatedUser authenticatedUser, @PathVariable("memberId") Long memberId, @PathVariable("shareDoyakId") Long shareDoyakId, @RequestBody ReqCommentDTO reqCommentDTO) {
        commentService.setCommentAdd(authenticatedUser.getMemberId(), shareDoyakId, reqCommentDTO);
        return ResponseEntity.ok("댓글 작성을 완료했습니다.");
    }

}
