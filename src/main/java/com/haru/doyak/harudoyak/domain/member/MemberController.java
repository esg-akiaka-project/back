package com.haru.doyak.harudoyak.domain.member;

import com.haru.doyak.harudoyak.annotation.Authenticated;
import com.haru.doyak.harudoyak.domain.comment.CommentService;
import com.haru.doyak.harudoyak.domain.sharedoyak.ShareDoyakService;
import com.haru.doyak.harudoyak.dto.member.ChangeMemberInfoReqDTO;
import com.haru.doyak.harudoyak.dto.comment.ResCommentDTO;
import com.haru.doyak.harudoyak.dto.member.MemberResDTO;
import com.haru.doyak.harudoyak.dto.member.MypageResDTO;
import com.haru.doyak.harudoyak.dto.sharedoyak.ResShareDoyakDTO;
import com.haru.doyak.harudoyak.exception.CustomException;
import com.haru.doyak.harudoyak.exception.ErrorCode;
import com.haru.doyak.harudoyak.security.AuthenticatedUser;
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
    public ResponseEntity<List<ResCommentDTO>> getMemberCommentList(@Authenticated AuthenticatedUser authenticatedUser, @PathVariable("memberId") Long memberId){
        List<ResCommentDTO> resCommentDTOS= commentService.getMemberCommentList(authenticatedUser.getMemberId());
        return ResponseEntity.ok(resCommentDTOS);
    }

    /*
    * 회원의 서로도약 글 모아보기
    * @param : membaerId(Long)
    * */
    @GetMapping("/{memberId}/posts")
    public ResponseEntity<List<ResShareDoyakDTO.ResMemberShareDoyakDYO>> getMemberShareDoyakList(@Authenticated AuthenticatedUser authenticatedUser, @PathVariable("memberId") Long memberId){
        List<ResShareDoyakDTO.ResMemberShareDoyakDYO> resMemberShareDoyakDYOS = shareDoyakService.getMemberShareDoyakList(authenticatedUser.getMemberId());
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
    public ResponseEntity chageNickname(@Authenticated AuthenticatedUser authenticatedUser,
                                        @PathVariable("memberId") Long memberId,
                                        @RequestBody ChangeMemberInfoReqDTO dto)
    {
        if(dto.getNickname()==null) throw new CustomException(ErrorCode.NULL_VALUE);

        if(memberService.isNicknameAvailable(dto.getNickname())){
            MemberResDTO res = memberService.changeNickname(authenticatedUser.getMemberId(), dto.getNickname());
            return ResponseEntity.ok().body(res);
        }else{
            throw new CustomException(ErrorCode.DUPLICATE_NICKNAME);
        }
    }

    @PutMapping("{memberId}/aiNickname")
    public ResponseEntity changeAiNickname(@Authenticated AuthenticatedUser authenticatedUser,
                                           @PathVariable("memberId") Long memberId,
                                           @RequestBody ChangeMemberInfoReqDTO dto){
        if(dto.getAiNickname()==null) throw new CustomException(ErrorCode.NULL_VALUE);
        MemberResDTO res = memberService.changeAiNickname(authenticatedUser.getMemberId(), dto.getAiNickname());
        return ResponseEntity.ok().body(res);
    }

    @PutMapping("{memberId}/goalName")
    public ResponseEntity changeGoalName(@Authenticated AuthenticatedUser authenticatedUser,
                                         @PathVariable("memberId") Long memberId,
                                         @RequestBody ChangeMemberInfoReqDTO dto){
        if(dto.getGoalName()==null) throw new CustomException(ErrorCode.NULL_VALUE);
        MemberResDTO res = memberService.changeGoalName(authenticatedUser.getMemberId(), dto.getGoalName());
        return ResponseEntity.ok().body(res);
    }

    @PutMapping("{memberId}/pwd")
    public ResponseEntity changePassword(@Authenticated AuthenticatedUser authenticatedUser,
                                         @PathVariable("memberId") Long memberId,
                                         @RequestBody ChangeMemberInfoReqDTO dto){
        if(dto.getPassword()==null) throw new CustomException(ErrorCode.NULL_VALUE);
        memberService.changePassword(authenticatedUser.getMemberId(), dto.getPassword());
        return ResponseEntity.ok().body("비밀번호가 변경되었습니다.");
    }

    @PostMapping("{memberId}/pwd")
    public ResponseEntity confirmPwd(@Authenticated AuthenticatedUser authenticatedUser,
                                     @PathVariable("memberId") Long memberId,
                                     @RequestBody ChangeMemberInfoReqDTO dto){
        if(dto.getPassword()==null) throw new CustomException(ErrorCode.NULL_VALUE);
        if(memberService.isCorrectPassword(authenticatedUser.getMemberId(), dto.getPassword())) {
            return ResponseEntity.ok().body("비밀번호가 일치합니다.");
        }
        throw new CustomException(ErrorCode.INVALID_PASSWORD);
    }

    @PutMapping("{memberId}/profile")
    public ResponseEntity changeProfile(@Authenticated AuthenticatedUser authenticatedUser,
                                        @PathVariable("memberId") Long memberId,
                                        @RequestBody ChangeMemberInfoReqDTO dto){
        if(dto.getPhotoUrl()==null) throw new CustomException(ErrorCode.NULL_VALUE);
        MemberResDTO res = memberService.changeProfilePhoto(authenticatedUser.getMemberId(), dto.getPhotoUrl());
        return ResponseEntity.ok().body(res);
    }

    @GetMapping("{memberId}/mypage")
    public ResponseEntity getMypage(@Authenticated AuthenticatedUser authenticatedUser,
                                    @PathVariable("memberId") Long memberId){
        MypageResDTO dto = memberService.getMypageInfo(authenticatedUser.getMemberId());
        return ResponseEntity.ok().body(dto);
    }

}
