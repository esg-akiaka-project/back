package com.haru.doyak.harudoyak.domain.member;

import com.haru.doyak.harudoyak.entity.Member;
import com.haru.doyak.harudoyak.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

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

    /**
     *
     * @param memberId
     * @param password
     * @return 비밀번호가 맞으면 true, 틀리면 false
     */
    public boolean isCorrectPassword(Long memberId, String password){
        Member member = memberRepository.findMemberById(memberId).orElseThrow();
        return passwordEncoder.matches(password, member.getPassword());
    }
    
}
