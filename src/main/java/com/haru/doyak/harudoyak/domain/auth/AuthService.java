package com.haru.doyak.harudoyak.domain.auth;

import com.haru.doyak.harudoyak.dto.auth.JoinReqDTO;
import com.haru.doyak.harudoyak.dto.auth.LoginReqDTO;
import com.haru.doyak.harudoyak.dto.auth.LoginResDTO;
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

import java.security.SecureRandom;
import java.util.Objects;
import java.util.Optional;

import static com.haru.doyak.harudoyak.entity.QFile.file;
import static com.haru.doyak.harudoyak.entity.QLevel.level;
import static com.haru.doyak.harudoyak.entity.QMember.member;

@RequiredArgsConstructor
@Service
public class AuthService {
    private final MemberRepository memberRepository;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;
    private final LevelRepository levelRepository;
    private final FileRepository fileRepository;
    private final EmailService emailService;
    @Value("${spring.oauth2.local.client-name}")
    private String local_client_name;

    public void joinMember(JoinReqDTO joinReqDTO){
        Optional<Member> optionalMember = memberRepository.findMemberByEmail(joinReqDTO.getEmail());
        if(optionalMember.isPresent()){
            Member savedMember = optionalMember.get();
            // 자체가입 한 경우 예외처리
            if(savedMember.getProviderId()==null) throw new CustomException(ErrorCode.DUPLICATE_MEMBER);
            // 소셜로 가입했던 경우 연동
            savedMember.updatePassword(passwordEncoder.encode(joinReqDTO.getPassword()));
            memberRepository.save(savedMember);
        }else {
            // 최초가입
            Member member = Member.builder()
                    .email(joinReqDTO.getEmail())
                    .password(passwordEncoder.encode(joinReqDTO.getPassword()))
                    .nickname(joinReqDTO.getNickname())
                    .isVerified(true)
                    .build();
            memberRepository.save(member);
            // 레벨 생성하기
            Level level = Level.builder()
                    .memberId(member.getMemberId())
                    .point(5L)// 가입시 5포인트
                    .build();
            levelRepository.save(level);
        }
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
        return memberRepository.findLevelAndFileByMemberId(memberId)
                .orElseThrow(()-> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
    }

    public void logout(Long memberId, String refreshToken) {
        Member member = memberRepository.findMemberByRefreshToken(refreshToken)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_TOKEN));
        if(!Objects.equals(member.getMemberId(), memberId)) throw new CustomException(ErrorCode.DIFFERENT_MEMBER_TOKEN);
        member.updateRefreshToken(null);
        memberRepository.save(member);
    }

    public void handleTempPasswordRequest(String email) {
        String tempPassword = generateTempPassword(8);
        saveTempPassword(email, tempPassword);
        emailService.sendTempPasswordEmail(email, tempPassword);
    }

    /**
     * 
     * @param passwordLength 원하는 임시 비밀번호의 길이
     * @return 랜덤으로 만들어진 임시 비밀번호
     */
    public String generateTempPassword(int passwordLength) {
        SecureRandom secureRandom = new SecureRandom();
        String PASSWORD_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

        StringBuilder sb = new StringBuilder(passwordLength);
        for(int i=0; i<passwordLength; i++){
            int index = secureRandom.nextInt(PASSWORD_CHARACTERS.length());
            sb.append(PASSWORD_CHARACTERS.charAt(index));
        }
        return sb.toString();
    }

    /**
     * @param email
     * @param tempPassword 인코딩 되지 않은 비밀번호
     */
    public void saveTempPassword(String email, String tempPassword){
        Member member = memberRepository.findMemberByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        member.updatePassword(passwordEncoder.encode(tempPassword));
        memberRepository.save(member);
    }
}
