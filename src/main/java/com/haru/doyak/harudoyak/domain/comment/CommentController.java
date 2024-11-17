package com.haru.doyak.harudoyak.domain.comment;

import com.haru.doyak.harudoyak.dto.comment.ReqCommentDTO;
import com.haru.doyak.harudoyak.dto.comment.ResCommentDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("api/posts/comments/*")
@RestController
public class CommentController {
    private final CommentService commentService;

    /*
     * 댓글 수정
     * @param : memberId(Long), commentId(Long)
     * */
    @PutMapping("{memberId}/{commentId}")
    public ResponseEntity<String> setCommentUpdate(@PathVariable("memberId") Long memberId, @PathVariable("commentId") Long commentId, @RequestBody ReqCommentDTO reqCommentDTO) {
        long commentUpdateResult = commentService.setCommentUpdate(memberId, commentId, reqCommentDTO);
        if(commentUpdateResult == 1) {
            return ResponseEntity.ok("댓글 수정이 완료되었습니다.");
        }
        return ResponseEntity.notFound().build();
    }

    /*
     * 댓글 삭제
     * @param : memberId(Long), commentId(Long)
     * */
    @DeleteMapping("{memberId}/{commentId}")
    public ResponseEntity<String> setCommentDelete(@PathVariable("memberId") Long memberId, @PathVariable("commentId") Long commentId){
        long commentDeleteResult = commentService.setCommentDelete(memberId, commentId);
        if(commentDeleteResult == 1){
            return ResponseEntity.ok("댓글 삭제가 완료되었습니다.");
        }
        return ResponseEntity.notFound().build();
    }

    /*
     * 댓글 목록
     * @param : shareDoyakId(Long)
     * @return :
     * */
    @GetMapping("list/{shareDoyakId}")
    public ResponseEntity<List<ResCommentDTO.ResCommentDetailDTO>> getCommentList(@PathVariable("shareDoyakId") Long shareDoyakId){
        List<ResCommentDTO.ResCommentDetailDTO> resReplyCommentDTOS = commentService.getCommentList(shareDoyakId);
        return ResponseEntity.ok(resReplyCommentDTOS);
    }

    /*
     * 대댓글 작성
     * @param : memberId(Long), shareDoyakId(Long), commentId(Long)
     * @return :
     * */
    @PostMapping("replys/{memberId}/{shareDoyakId}/{commentId}")
    public ResponseEntity<String> setCommentChildAdd(@PathVariable("memberId") Long memberId, @PathVariable("shareDoyakId") Long shareDoyakId, @PathVariable("commentId") Long commentId, @RequestBody ReqCommentDTO reqCommentDTO) {
        if(commentId != null){
            reqCommentDTO.setParentCommentId(commentId);
            commentService.setCommentAdd(memberId,shareDoyakId, reqCommentDTO);
            return ResponseEntity.ok("대댓글 작성을 완료했습니다.");
        }
        return ResponseEntity.notFound().build();
    }

    /*
     * 댓글 작성
     * @param : memberId(Long), shareDoyakId(Long), commentContent(String)
     * @return :
     * */
    @PostMapping("{memberId}/{shareDoyakId}")
    public ResponseEntity<String> setCommentAdd(@PathVariable("memberId") Long memberId, @PathVariable("shareDoyakId") Long shareDoyakId, @RequestBody ReqCommentDTO reqCommentDTO) {
        commentService.setCommentAdd(memberId, shareDoyakId, reqCommentDTO);
        return ResponseEntity.ok("댓글 작성을 완료했습니다.");
    }

}
