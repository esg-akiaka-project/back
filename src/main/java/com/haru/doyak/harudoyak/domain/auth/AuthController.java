package com.haru.doyak.harudoyak.domain.auth;

import com.haru.doyak.harudoyak.annotation.Authenticated;
import com.haru.doyak.harudoyak.domain.auth.oauth.OAuthService;
import com.haru.doyak.harudoyak.domain.member.MemberService;
import com.haru.doyak.harudoyak.dto.auth.*;
import com.haru.doyak.harudoyak.dto.auth.jwt.JwtMemberDTO;
import com.haru.doyak.harudoyak.dto.auth.jwt.JwtReqDTO;
import com.haru.doyak.harudoyak.dto.auth.jwt.JwtResDTO;
import com.haru.doyak.harudoyak.exception.CustomException;
import com.haru.doyak.harudoyak.exception.ErrorCode;
import com.haru.doyak.harudoyak.security.AuthenticatedUser;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/auth")
public class AuthController {
    private final OAuthService oAuthService;
    private final AuthService authService;
    private final EmailService emailService;
    private final MemberService memberService;

    @PostMapping("login")
    public ResponseEntity<LoginResDTO> login(@RequestBody LoginReqDTO loginReqDTO) {
        JwtMemberDTO jwtMemberDTO = authService.login(loginReqDTO);
        LoginResDTO loginResDTO = authService.makeLoginResDTO(jwtMemberDTO.getMember().getMemberId());
        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, jwtMemberDTO.getJwtRecord().authorizationType()+" "+jwtMemberDTO.getJwtRecord().accessToken())
                .body(loginResDTO);
    }

    @PostMapping("login/google")
    public ResponseEntity<LoginResDTO> googleLogin(@RequestBody LoginReqDTO loginReqDTO){
        JwtMemberDTO jwtMemberDTO = oAuthService.googleLogin(loginReqDTO.getCode());
        LoginResDTO loginResDTO = authService.makeLoginResDTO(jwtMemberDTO.getMember().getMemberId());
        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, jwtMemberDTO.getJwtRecord().authorizationType()+" "+jwtMemberDTO.getJwtRecord().accessToken())
                .body(loginResDTO);
    }

    @PostMapping("login/kakao")
    public ResponseEntity kakaoLogin(@RequestBody LoginReqDTO loginReqDTO) {
        JwtMemberDTO jwtMemberDTO = oAuthService.kakaoLogin(loginReqDTO.getCode());
        LoginResDTO loginResDTO = authService.makeLoginResDTO(jwtMemberDTO.getMember().getMemberId());
        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, jwtMemberDTO.getJwtRecord().authorizationType()+" "+jwtMemberDTO.getJwtRecord().accessToken())
                .body(loginResDTO);
    }

    @PostMapping("join")
    public ResponseEntity<String> join(@RequestBody JoinReqDTO joinReqDto){
        if(joinReqDto.getIsVerified()==null || !joinReqDto.getIsVerified()) throw new CustomException(ErrorCode.EMAIL_NOT_VERIFIED);
        authService.joinMember(joinReqDto);
        return ResponseEntity.ok().body("회원가입이 완료되었습니다.");
    }

    @PostMapping("email/verify")
    public ResponseEntity<String> emailVerify(@RequestHeader(value = "Referer", required = false) String referer,
                                              @RequestBody EmailVerifyReqDTO dto)
    {
        if(!memberService.isEmailAvailable(dto.getEmail())) {
            throw new CustomException(ErrorCode.DUPLICATE_MEMBER);
        }
        emailService.sendAuthLinkEmail(referer, dto.getEmail());
        return ResponseEntity.ok().body("인증 메일이 발송되었습니다.");

    }

    @PostMapping("validate")
    public ResponseEntity validateMemberId(@Authenticated AuthenticatedUser authenticatedUser){
        return ResponseEntity.ok().body(authenticatedUser.toString());
    }

    @PostMapping("reissue")
    public ResponseEntity<JwtResDTO> reissue(@RequestBody JwtReqDTO jwtReqDTO){
        if(jwtReqDTO.getRefreshToken()==null) throw new CustomException(ErrorCode.NULL_VALUE);
        JwtMemberDTO jwtMemberDTO = authService.reissue(jwtReqDTO);
        JwtResDTO jwtResDTO = JwtResDTO.builder()
                .memberId(jwtMemberDTO.getMember().getMemberId())
                .refreshToken(jwtMemberDTO.getJwtRecord().refreshToken())
                .build();
        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, jwtMemberDTO.getJwtRecord().authorizationType()+" "+jwtMemberDTO.getJwtRecord().accessToken())
                .body(jwtResDTO);
    }

    @PostMapping("logout/{memberId}")
    public ResponseEntity logout(@Authenticated AuthenticatedUser authenticatedUser,
                                 @PathVariable("memberId") Long memberId,
                                 @RequestBody JwtReqDTO jwtReqDTO)
    {
        if(jwtReqDTO.getRefreshToken()==null) throw new CustomException(ErrorCode.NULL_VALUE);

        authService.logout(authenticatedUser.getMemberId(), jwtReqDTO.getRefreshToken());
        return ResponseEntity.ok().body("로그아웃이 완료되었습니다.");
    }

    @PostMapping("temp-password")
    public ResponseEntity sendTempPassword(@Authenticated AuthenticatedUser authenticatedUser,
                                           @RequestBody JoinReqDTO joinReqDTO){
        if(joinReqDTO.getEmail() == null) throw new CustomException(ErrorCode.NULL_VALUE);
        
        authService.handleTempPasswordRequest(joinReqDTO.getEmail());
        return ResponseEntity.ok().body("임시 비밀번호가 전송됨");
    }
}
