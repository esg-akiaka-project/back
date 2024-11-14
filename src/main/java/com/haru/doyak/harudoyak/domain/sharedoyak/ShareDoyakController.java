package com.haru.doyak.harudoyak.domain.sharedoyak;

import com.haru.doyak.harudoyak.dto.sharedoyak.ReqCommentDTO;
import com.haru.doyak.harudoyak.dto.sharedoyak.ReqShareDoyakDTO;
import com.haru.doyak.harudoyak.dto.sharedoyak.ResReplyCommentDTO;
import com.haru.doyak.harudoyak.dto.sharedoyak.ResShareDoyakDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/posts/*")
@Slf4j
public class ShareDoyakController {
    private final ShareDoyakService shareDoyakService;
    /*
    * 서로도약 커뮤니티
    * */

    /*
    * 댓글 삭제
    * @param : memberId(Long), commentId(Long)
    * */
    @DeleteMapping("comments/{memberId}/{commentId}")
    public ResponseEntity<String> setCommentDelete(@PathVariable("memberId") Long memberId, @PathVariable("commentId") Long commentId){
       long commentDeleteResult = shareDoyakService.setCommentDelete(memberId, commentId);
       if(commentDeleteResult == 1){
           return ResponseEntity.ok("댓글 삭제가 완료되었습니다.");
       }
       return ResponseEntity.notFound().build();
    }

    /*
    * 서로도약 삭제
    * @param : memberId(Long), shareDoyakId(Long)
    * */
    @DeleteMapping("{memberId}/{shareDoyakId}")
    public ResponseEntity<String> setShareDoyakDelete(@PathVariable("memberId") Long memberId, @PathVariable Long shareDoyakId) {
        long shareDoyakDeleteResult = shareDoyakService.setShareDoyakDelete(memberId, shareDoyakId);
        if(shareDoyakDeleteResult == 1) {
            return ResponseEntity.ok("서로도약 삭제가 완료되었습니다.");
        }
        return ResponseEntity.notFound().build();
    }
    /*
    * 댓글 수정
    * @param : memberId(Long), commentId(Long)
    * */
    @PutMapping("comments/{memberId}/{commentId}")
    public ResponseEntity<String> setCommentUpdate(@PathVariable("memberId") Long memberId, @PathVariable("commentId") Long commentId, @RequestBody ReqCommentDTO reqCommentDTO) {
        long commentUpdateResult = shareDoyakService.setCommentUpdate(memberId, commentId, reqCommentDTO);
        if(commentUpdateResult == 1) {
            return ResponseEntity.ok("댓글 수정이 완료되었습니다.");
        }
        return ResponseEntity.notFound().build();
    }

    /*
     * 서로도약 수정
     * @param : memberId(Long), shareDoyakId(Long), shareContent(String)
     * @return :
     * */
    @PutMapping("{memberId}/{shareDoyakId}")
    public ResponseEntity<String> setShareDoyakUpdate(@PathVariable("memberId") Long memberId, @PathVariable("shareDoyakId") Long shareDoyakId,@RequestBody ReqShareDoyakDTO reqShareDoyakDTO){
        long shareDoyakUpdateResult = shareDoyakService.setShareDoyakUpdate(memberId, shareDoyakId, reqShareDoyakDTO);
        if(shareDoyakUpdateResult == 1){
            return ResponseEntity.ok("서로도약 게시글 수정이 완료되었습니다.");
        }
        return ResponseEntity.notFound().build();
    }

    /*
     * 댓글 목록
     * @param : shareDoyakId(Long)
     * @return :
     * */
    @GetMapping("comments/list/{shareDoyakId}")
    public ResponseEntity<List<ResReplyCommentDTO>> getCommentList(@PathVariable("shareDoyakId") Long shareDoyakId){
        List<ResReplyCommentDTO> resReplyCommentDTOS = shareDoyakService.getCommentList(shareDoyakId);
        return ResponseEntity.ok(resReplyCommentDTOS);
    }

    /*
     * 서로도약 목록
     * @param :
     * @return : List<ResShareDoyakDTO>
     * */
    @GetMapping("/list")
    public ResponseEntity<List<ResShareDoyakDTO>> getShareDoyakList(){
        List<ResShareDoyakDTO> resShareDoyakDTOS = shareDoyakService.getShareDoyakList();
        return ResponseEntity.ok(resShareDoyakDTOS);
    }

    /*
     * 대댓글 작성
     * @param : memberId(Long), shareDoyakId(Long), commentId(Long)
     * @return :
     * */
    @PostMapping("comments/{memberId}/{shareDoyakId}/{commentId}")
    public void setCommentChildAdd(@PathVariable("memberId") Long memberId, @PathVariable("shareDoyakId") Long shareDoyakId, @PathVariable("commentId") Long commentId) {



    }

    /*
     * 댓글 작성
     * @param : memberId(Long), shareDoyakId(Long), commentContent(String)
     * @return :
     * */
    @PostMapping("comments/{memberId}/{shareDoyakId}")
    public ResponseEntity<String> setCommentAdd(@PathVariable("memberId") Long memberId, @PathVariable("shareDoyakId") Long shareDoyakId, @RequestBody ReqCommentDTO reqCommentDTO) {
        shareDoyakService.setCommentAdd(memberId, shareDoyakId, reqCommentDTO);
        return ResponseEntity.ok("댓글 작성을 완료했습니다.");
    }

    /*
    * 도약하기 추가
    * req : memberId(Long), shareDoyakId(Long)
    * res : doyakCount(Long)
     * */
    @PostMapping("doyak/{memberId}/{shareDoyakId}")
    public ResponseEntity<Long> setDoyakAdd(@PathVariable("memberId") Long memberId, @PathVariable("shareDoyakId") Long shareDoyakId) {
        Long doyakCount = shareDoyakService.setDoyakAdd(memberId, shareDoyakId);
        return ResponseEntity.ok().body(doyakCount);
    }

    /*
    * 서로도약 작성
    * @param : memberId(Long), shareContent(String), shareImegeUrl(String), shareOriginalName(String)
    * @return :
    * */
    @PostMapping("{memberId}")
    public ResponseEntity<String> setShareDoyakAdd(@PathVariable("memberId") Long memberId, @RequestBody ReqShareDoyakDTO reqShareDoyakDTO) {
        shareDoyakService.setShareDoyakAdd(memberId, reqShareDoyakDTO);
        return ResponseEntity.ok("서로도약 게시글 작성을 완료했습니다.");
    }

}
