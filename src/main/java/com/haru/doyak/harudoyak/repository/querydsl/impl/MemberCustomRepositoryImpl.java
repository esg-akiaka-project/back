package com.haru.doyak.harudoyak.repository.querydsl.impl;

import com.haru.doyak.harudoyak.entity.Member;

import static com.haru.doyak.harudoyak.entity.QMember.member;

import com.haru.doyak.harudoyak.repository.querydsl.MemberCustomRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@RequiredArgsConstructor
public class MemberCustomRepositoryImpl implements MemberCustomRepository {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Optional<Member> findMemberByEmail(String email){
        Member getMember = jpaQueryFactory.selectFrom(member)
                .where(member.email.eq(email))
                .fetchOne();
        return Optional.ofNullable(getMember);
    }

    @Override
    public Optional<Member> findMemberByNickname(String nickname) {
        Member getMember = jpaQueryFactory.selectFrom(member)
                .where(member.nickname.eq(nickname))
                .fetchOne();
        return Optional.ofNullable(getMember);
    }

    @Override
    public Optional<Member> findMemberById(Long id) {
        Member getMember = jpaQueryFactory.selectFrom(member)
                .where(member.memberId.eq(id))
                .fetchOne();
        return Optional.ofNullable(getMember);
    }

    @Override
    public Optional<Member> findMemberByRefreshToken(String refreshToken) {
        Member getMember = jpaQueryFactory.selectFrom(member)
                .where(member.refreshToken.eq(refreshToken))
                .fetchOne();
        return Optional.ofNullable(getMember);
    }

    @Override
    public Optional<Member> findMemberByGoogleId(String googleId) {
        Member getMember = jpaQueryFactory.selectFrom(member)
                .where(member.googleId.eq(googleId))
                .fetchOne();
        return Optional.ofNullable(getMember);
    }

    @Override
    public Optional<Member> findMemberByKakaoId(String kakaoId) {
        Member getMember = jpaQueryFactory.selectFrom(member)
                .where(member.kakaoId.eq(kakaoId))
                .fetchOne();
        return Optional.ofNullable(getMember);
    }

    @Override
    public Optional<Member> findMemberByProviderId(String providerId) {
        Member getMember = jpaQueryFactory.selectFrom(member)
                .where(member.providerId.eq(providerId))
                .fetchOne();
        return Optional.ofNullable(getMember);
    }


}
