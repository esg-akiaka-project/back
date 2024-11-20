package com.haru.doyak.harudoyak.domain.member;

import com.haru.doyak.harudoyak.domain.comment.CommentService;
import com.haru.doyak.harudoyak.domain.sharedoyak.ShareDoyakService;
import com.haru.doyak.harudoyak.dto.member.ChangeMemberInfoReqDTO;
import com.haru.doyak.harudoyak.dto.comment.ResCommentDTO;
import com.haru.doyak.harudoyak.dto.member.MemberResDTO;
import com.haru.doyak.harudoyak.dto.sharedoyak.ResShareDoyakDTO;
import com.haru.doyak.harudoyak.exception.CustomException;
import com.haru.doyak.harudoyak.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/members")
@Slf4j
public class MemberController {
    private final MemberService memberService;
    private final ShareDoyakService shareDoyakService;
    private final CommentService commentService;

    /*
     * 회원의 댓글 모아보기
     * @param : membaerId(Long)
     * */
    @GetMapping("/{memberId}/comments")
    public ResponseEntity<List<ResCommentDTO>> getMemberCommentList(@PathVariable("memberId") Long memberId){
        List<ResCommentDTO> resCommentDTOS= commentService.getMemberCommentList(memberId);
        return ResponseEntity.ok(resCommentDTOS);
    }

    /*
    * 회원의 서로도약 글 모아보기
    * @param : membaerId(Long)
    * */
    @GetMapping("/{memberId}/posts")
    public ResponseEntity<List<ResShareDoyakDTO.ResMemberShareDoyakDYO>> getMemberShareDoyakList(@PathVariable("memberId") Long memberId){
        List<ResShareDoyakDTO.ResMemberShareDoyakDYO> resMemberShareDoyakDYOS = shareDoyakService.getMemberShareDoyakList(memberId);
        return ResponseEntity.ok(resMemberShareDoyakDYOS);
    }

    @GetMapping("check")
    public ResponseEntity check(@RequestParam("nickname")String nickname) {
        if(memberService.isNicknameAvailable(nickname)){
            MemberResDTO res = MemberResDTO.builder()
                    .nickname(nickname)
                    .build();
            return ResponseEntity.ok().body(res);
        }else throw new CustomException(ErrorCode.DUPLICATE_NICKNAME);
    }

    @PutMapping("{memberId}/nickname")
    public ResponseEntity chageNickname(@PathVariable("memberId") Long memberId,
                                        @RequestBody ChangeMemberInfoReqDTO dto)
    {
        if(dto.getNickname()==null) throw new CustomException(ErrorCode.NULL_VALUE);

        if(memberService.isNicknameAvailable(dto.getNickname())){
            MemberResDTO res = memberService.changeNickname(memberId, dto.getNickname());
            return ResponseEntity.ok().body(res);
        }else{
            throw new CustomException(ErrorCode.DUPLICATE_NICKNAME);
        }
    }

    @PutMapping("{memberId}/aiNickname")
    public ResponseEntity changeAiNickname(@PathVariable("memberId") Long memberId,
                                           @RequestBody ChangeMemberInfoReqDTO dto){
        if(dto.getAiNickname()==null) throw new CustomException(ErrorCode.NULL_VALUE);
        MemberResDTO res = memberService.changeAiNickname(memberId, dto.getAiNickname());
        return ResponseEntity.ok().body(res);
    }

    @PutMapping("{memberId}/goalName")
    public ResponseEntity changeGoalName(@PathVariable("memberId") Long memberId,
                                         @RequestBody ChangeMemberInfoReqDTO dto){
        if(dto.getGoalName()==null) throw new CustomException(ErrorCode.NULL_VALUE);
        MemberResDTO res = memberService.changeGoalName(memberId, dto.getGoalName());
        return ResponseEntity.ok().body(res);
    }

    @PutMapping("{memberId}/pwd")
    public ResponseEntity changePassword(@PathVariable("memberId") Long memberId,
                                         @RequestBody ChangeMemberInfoReqDTO dto){
        if(dto.getPassword()==null) throw new CustomException(ErrorCode.NULL_VALUE);
        memberService.changePassword(memberId, dto.getPassword());
        return ResponseEntity.ok().body("비밀번호가 변경되었습니다.");
    }

    @PostMapping("{memberId}/pwd")
    public ResponseEntity confirmPwd(@PathVariable("memberId") Long memberId,
                                         @RequestBody ChangeMemberInfoReqDTO dto){
        if(dto.getPassword()==null) throw new CustomException(ErrorCode.NULL_VALUE);
        if(memberService.isCorrectPassword(memberId, dto.getPassword())) {
            return ResponseEntity.ok().body("비밀번호가 일치합니다.");
        }
        throw new CustomException(ErrorCode.INVALID_PASSWORD);
    }

    @PutMapping("{memberId}/profile")
    public ResponseEntity changeProfile(@PathVariable("memberId") Long memberId,
                                        @RequestBody ChangeMemberInfoReqDTO dto){
        if(dto.getPhotoUrl()==null) throw new CustomException(ErrorCode.NULL_VALUE);
        MemberResDTO res = memberService.changeProfilePhoto(memberId, dto.getPhotoUrl());
        return ResponseEntity.ok().body(res);
    }

}
