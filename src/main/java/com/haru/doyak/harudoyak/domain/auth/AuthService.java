package com.haru.doyak.harudoyak.domain.auth;

import static com.haru.doyak.harudoyak.entity.QMember.member;
import static com.haru.doyak.harudoyak.entity.QLevel.level;
import static com.haru.doyak.harudoyak.entity.QFile.file;

import com.haru.doyak.harudoyak.dto.auth.*;
import com.haru.doyak.harudoyak.dto.auth.jwt.JwtMemberDTO;
import com.haru.doyak.harudoyak.dto.auth.jwt.JwtRecord;
import com.haru.doyak.harudoyak.dto.auth.jwt.JwtReqDTO;
import com.haru.doyak.harudoyak.entity.Level;
import com.haru.doyak.harudoyak.entity.Member;
import com.haru.doyak.harudoyak.exception.CustomException;
import com.haru.doyak.harudoyak.exception.ErrorCode;
import com.haru.doyak.harudoyak.repository.FileRepository;
import com.haru.doyak.harudoyak.repository.LevelRepository;
import com.haru.doyak.harudoyak.repository.MemberRepository;
import com.haru.doyak.harudoyak.security.JwtProvider;
import com.querydsl.core.Tuple;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class AuthService {
    private final MemberRepository memberRepository;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;
    private final LevelRepository levelRepository;
    private final FileRepository fileRepository;
    @Value("${spring.oauth2.local.client-name}")
    private String local_client_name;

    public void joinMember(JoinReqDTO joinReqDTO){
        if(memberRepository.findMemberByEmail(joinReqDTO.getEmail()).isPresent()){
            throw new CustomException(ErrorCode.DUPLICATE_MEMBER);
        }
        Member member = Member.builder()
                .email(joinReqDTO.getEmail())
                .provider(local_client_name)
                .password(passwordEncoder.encode(joinReqDTO.getPassword()))
                .nickname(joinReqDTO.getNickname())
                .build();

        memberRepository.save(member);
        member.updateLocalProviderId();
        memberRepository.save(member);
        // 레벨 생성하기
        Level level = Level.builder()
                .memberId(member.getMemberId())
                .point(5L)// 가입시 5포인트
                .build();
        levelRepository.save(level);
    }

    /**
     * @param loginReqDTO email, password
     * @return 일치하는 member를 찾아 jwt를 생성해 member와 jwt를 반환
     * @throws Exception 가입되지 않은 경우, 비밀번호가 틀릴 경우
     */
    public JwtMemberDTO login(LoginReqDTO loginReqDTO) {
        Optional<Member> memberOptional = memberRepository.findMemberByEmail(loginReqDTO.getEmail());
        Member savedMember = memberOptional
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        if(!passwordEncoder.matches(loginReqDTO.getPassword(), savedMember.getPassword())){
            throw new CustomException(ErrorCode.INVALID_PASSWORD);
        }
        // 토큰 발행
        JwtRecord jwtRecord = jwtProvider.getJwtRecord(savedMember);
        savedMember.updateRefreshToken(jwtRecord.refreshToken());
        memberRepository.save(savedMember);
        return JwtMemberDTO.builder()
                .jwtRecord(jwtRecord)
                .member(savedMember)
                .build();
    }

    /**
     *
     * @param jwtReqDTO refresh token 만 사용
     * @return rtk 로 유저를 찾고, access, refresh 모두 새로 발급해 JwtMemberDTO반환
     */
    public JwtMemberDTO reissue(JwtReqDTO jwtReqDTO) {
        // 검증
        if(jwtProvider.validateAndExtractClaims(jwtReqDTO.getRefreshToken(), "refresh")!=null){
            // rtk로 유저 찾기
            Member savedMember = memberRepository.findMemberByRefreshToken(jwtReqDTO.getRefreshToken())
                    .orElseThrow(()-> new CustomException(ErrorCode.INVALID_TOKEN));
            // 재발급
            JwtRecord jwtRecord = jwtProvider.getJwtRecord(savedMember);
            savedMember.updateRefreshToken(jwtRecord.refreshToken());
            memberRepository.save(savedMember);
            return JwtMemberDTO.builder()
                    .jwtRecord(jwtRecord)
                    .member(savedMember)
                    .build();
        }
        throw new CustomException(ErrorCode.INVALID_TOKEN);
    }

    public LoginResDTO makeLoginResDTO(Long memberId) {
        Tuple tuple = memberRepository.findLevelAndFileByMemberId(memberId)
                .orElseThrow(()-> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        return LoginResDTO.builder()
                .member(tuple.get(member))
                .level(tuple.get(level))
                .file(tuple.get(file))
                .build();
    }

    public boolean logout(Long memberId, String refreshToken) {
        Member member = memberRepository.findMemberByRefreshToken(refreshToken)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_TOKEN));
        if(!Objects.equals(member.getMemberId(), memberId)) throw new CustomException(ErrorCode.DIFFERENT_MEMBER_TOKEN);
        member.updateRefreshToken(null);
        memberRepository.save(member);
        return true;
    }
}
