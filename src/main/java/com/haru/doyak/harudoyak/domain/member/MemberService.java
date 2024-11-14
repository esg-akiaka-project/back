package com.haru.doyak.harudoyak.domain.member;

import com.haru.doyak.harudoyak.entity.File;
import com.haru.doyak.harudoyak.entity.Member;
import static com.haru.doyak.harudoyak.entity.QFile.file;
import static com.haru.doyak.harudoyak.entity.QMember.member;

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

    public void changeNickname(Long memberId, String newNickname) {
        Optional<Member> optionalMember = memberRepository.findMemberById(memberId);
        if(optionalMember.isEmpty()) return;
        Member member = optionalMember.get();
        member.updateNickname(newNickname);
        memberRepository.save(member);
    }

    public void changeAiNickname(Long memberId, String newAiNickname) {
        Optional<Member> optionalMember = memberRepository.findMemberById(memberId);
        if(optionalMember.isEmpty()) return;
        Member member = optionalMember.get();
        member.updateAiNickname(newAiNickname);
        memberRepository.save(member);
    }

    public void changeGoalName(Long memberId, String newGoalName) {
        Optional<Member> optionalMember = memberRepository.findMemberById(memberId);
        if(optionalMember.isEmpty()) return;
        Member member = optionalMember.get();
        member.updateGoalName(newGoalName);
        memberRepository.save(member);
    }

    public void changePassword(Long memberId, String newPassword) {
        String encoded = passwordEncoder.encode(newPassword);
        Optional<Member> optionalMember = memberRepository.findMemberById(memberId);
        if(optionalMember.isEmpty()) return;
        Member member = optionalMember.get();
        member.updatePassword(encoded);
        memberRepository.save(member);
    }

    public void changeProfilePhoto(Long memberId, String newPhotoUrl) {
        Tuple tuple = memberRepository.findMemberFileByMemberId(memberId).orElseThrow();
        File getFile = tuple.get(file);
        if(getFile==null){
            // 기존 프로필이 없다면 생성 후 member와 연결
            getFile = File.builder()
                    .filePathName(newPhotoUrl)
                    .build();
            fileRepository.save(getFile);

            Member getMember = tuple.get(member);
            getMember.updateFileId(getFile.getFileId());
            memberRepository.save(getMember);
        }
        getFile.updateFilePathName(newPhotoUrl);
        fileRepository.save(getFile);
    }

    /**
     * @param memberId
     * @param password
     * @return 비밀번호가 맞으면 true, 틀리면 false
     */
    public boolean isCorrectPassword(Long memberId, String password){
        Member member = memberRepository.findMemberById(memberId).orElseThrow();
        return passwordEncoder.matches(password, member.getPassword());
    }
    
}
