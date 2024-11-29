package com.haru.doyak.harudoyak.domain.member;

import com.haru.doyak.harudoyak.dto.auth.LoginResDTO;
import com.haru.doyak.harudoyak.dto.member.MemberResDTO;
import com.haru.doyak.harudoyak.dto.member.MypageResDTO;
import com.haru.doyak.harudoyak.entity.File;
import com.haru.doyak.harudoyak.entity.Level;
import com.haru.doyak.harudoyak.entity.Member;
import static com.haru.doyak.harudoyak.entity.QFile.file;
import static com.haru.doyak.harudoyak.entity.QMember.member;
import static com.haru.doyak.harudoyak.entity.QLevel.level;

import com.haru.doyak.harudoyak.exception.CustomException;
import com.haru.doyak.harudoyak.exception.ErrorCode;
import com.haru.doyak.harudoyak.repository.FileRepository;
import com.haru.doyak.harudoyak.repository.MemberRepository;
import com.querydsl.core.Tuple;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final FileRepository fileRepository;

    /**
     *
     * @param nickname
     * @return 없는 nickname 이면 true, 존재하면 false
     */
    public boolean isNicknameAvailable(String nickname) {
        Optional<Member> optionalMember = memberRepository.findMemberByNickname(nickname);
        return optionalMember.isEmpty();
    }

    public boolean isEmailAvailable(String email) {
        // 존재하지 않으면 true
        // 존재할때 providernull이 아니면 가능
        Optional<Member> optionalMember = memberRepository.findMemberByEmail(email);

        if(optionalMember.isEmpty()){
            return true;
        }

        String provider = optionalMember.get().getProvider();
        if(provider != null) {// 구글 가입 한 적 있으면 자체가입 가능
            return true;
        }
        return false;
    }

    public MemberResDTO changeNickname(Long memberId, String newNickname) {
        Member member = getMemberById(memberId);
        member.updateNickname(newNickname);

        memberRepository.save(member);
        return MemberResDTO.builder()
                .nickname(member.getNickname())
                .build();
    }

    public MemberResDTO changeAiNickname(Long memberId, String newAiNickname) {
        Member member = getMemberById(memberId);
        member.updateAiNickname(newAiNickname);
        memberRepository.save(member);
        return MemberResDTO.builder()
                .aiNickname(member.getAiNickname())
                .build();
    }

    public MemberResDTO changeGoalName(Long memberId, String newGoalName) {
        Member member = getMemberById(memberId);
        member.updateGoalName(newGoalName);
        memberRepository.save(member);
        return MemberResDTO.builder()
                .goalName(member.getGoalName())
                .build();
    }

    public void changePassword(Long memberId, String oldPassword, String newPassword) {
        Member member = getMemberById(memberId);
        if(passwordEncoder.matches(oldPassword, member.getPassword())) {
            // 기존 비밀번호가 맞을 경우 패스워드 암호화 해서 저장
            member.updatePassword(passwordEncoder.encode(newPassword));
        }else throw new CustomException(ErrorCode.INVALID_PASSWORD);
        memberRepository.save(member);
    }

    public MemberResDTO changeProfilePhoto(Long memberId, String newPhotoUrl) {
        Member member = memberRepository.findFileByMemberId(memberId).orElseThrow();
        File file = member.getFile();
        if(file==null){
            // 기존 프로필이 없다면 생성 저장
            file = fileRepository.save(File.builder()
                            .filePathName(newPhotoUrl)
                    .build());
        }else {
            file.updateFilePathName(newPhotoUrl);
        }
        member.updateFile(file);
        memberRepository.save(member);
        return MemberResDTO.builder()
                .profileUrl(file.getFilePathName())
                .build();
    }

    /**
     * @param memberId
     * @param password
     * @return 비밀번호가 맞으면 true, 틀리면 false
     */
    public boolean isCorrectPassword(Long memberId, String password){
        Member member = getMemberById(memberId);
        return passwordEncoder.matches(password, member.getPassword());
    }

    public Member getMemberById(Long memberId) {
        Optional<Member> optionalMember = memberRepository.findMemberById(memberId);
        if(optionalMember.isEmpty()) throw new CustomException(ErrorCode.MEMBER_NOT_FOUND);
        return optionalMember.get();
    }

    public MypageResDTO getMypageInfo(Long memberId) {
        LoginResDTO loginResDTO = memberRepository.findLevelAndFileByMemberId(memberId)
                .orElseThrow(()->new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        return MypageResDTO.builder()
                .level(loginResDTO.getLevel())
                .file(loginResDTO.getFile())
                .build();
    }
}
