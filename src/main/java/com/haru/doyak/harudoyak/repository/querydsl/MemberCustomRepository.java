package com.haru.doyak.harudoyak.repository.querydsl;

import com.haru.doyak.harudoyak.dto.auth.LoginResDTO;
import com.haru.doyak.harudoyak.entity.Member;
import com.querydsl.core.Tuple;

import java.util.Optional;

public interface MemberCustomRepository {
    Optional<Member> findMemberByEmail(String email);
    Optional<Member> findMemberByNickname(String phone);
    Optional<Member> findMemberById(Long id);
    Optional<Member> findMemberByRefreshToken(String refreshToken);
    Optional<Member> findMemberByProviderId(String providerId);
    Optional<LoginResDTO> findLevelAndFileByMemberId(Long memberId);
    Optional<Member> findFileByMemberId(Long memberId);
}
