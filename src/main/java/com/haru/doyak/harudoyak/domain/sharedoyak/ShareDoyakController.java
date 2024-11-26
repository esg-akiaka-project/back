package com.haru.doyak.harudoyak.domain.sharedoyak;

import com.haru.doyak.harudoyak.annotation.Authenticated;
import com.haru.doyak.harudoyak.dto.sharedoyak.*;
import com.haru.doyak.harudoyak.security.AuthenticatedUser;
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
    * 서로도약 삭제
    * @param : memberId(Long), shareDoyakId(Long)
    * */
    @DeleteMapping("{memberId}/{shareDoyakId}")
    public ResponseEntity<String> setShareDoyakDelete(@Authenticated AuthenticatedUser authenticatedUser, @PathVariable("memberId") Long memberId, @PathVariable("shareDoyakId") Long shareDoyakId) {
        log.info("멤버아이디가 오니? {}", memberId);
        log.info("쉐어도약아이디는? {}", shareDoyakId);
        long shareDoyakDeleteResult = shareDoyakService.setShareDoyakDelete(authenticatedUser.getMemberId(), shareDoyakId);
        if(shareDoyakDeleteResult >= 1) {
            return ResponseEntity.ok("서로도약 삭제가 완료되었습니다.");
        }
        return ResponseEntity.notFound().build();
    }

    /*
     * 서로도약 수정
     * @param : memberId(Long), shareDoyakId(Long), shareContent(String)
     * @return :
     * */
    @PutMapping("{memberId}/{shareDoyakId}")
    public ResponseEntity<String> setShareDoyakUpdate(@Authenticated AuthenticatedUser authenticatedUser, @PathVariable("memberId") Long memberId, @PathVariable("shareDoyakId") Long shareDoyakId,@RequestBody ReqShareDoyakDTO reqShareDoyakDTO){
        long shareDoyakUpdateResult = shareDoyakService.setShareDoyakUpdate(authenticatedUser.getMemberId(), shareDoyakId, reqShareDoyakDTO);
        if(shareDoyakUpdateResult == 1){
            return ResponseEntity.ok("서로도약 게시글 수정이 완료되었습니다.");
        }
        return ResponseEntity.notFound().build();
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
    * 도약하기 추가
    * req : memberId(Long), shareDoyakId(Long)
    * res : doyakCount(Long)
     * */
    @PostMapping("doyak/{memberId}/{shareDoyakId}")
    public ResponseEntity<ResDoyakDTO> setDoyakAdd(@Authenticated AuthenticatedUser authenticatedUser, @PathVariable("memberId") Long memberId, @PathVariable("shareDoyakId") Long shareDoyakId) {
        ResDoyakDTO resDoyakDTO = shareDoyakService.setDoyakAdd(authenticatedUser.getMemberId(), shareDoyakId);
        return ResponseEntity.ok().body(resDoyakDTO);
    }

    /*
    * 서로도약 작성
    * @param : memberId(Long), shareContent(String), shareImegeUrl(String), shareOriginalName(String)
    * @return :
    * */
    @PostMapping("{memberId}")
    public ResponseEntity<String> setShareDoyakAdd(@Authenticated AuthenticatedUser authenticatedUser, @PathVariable("memberId") Long memberId, @RequestBody ReqShareDoyakDTO reqShareDoyakDTO) {
        shareDoyakService.setShareDoyakAdd(authenticatedUser.getMemberId(), reqShareDoyakDTO);
        return ResponseEntity.ok("서로도약 게시글 작성을 완료했습니다.");
    }

}
