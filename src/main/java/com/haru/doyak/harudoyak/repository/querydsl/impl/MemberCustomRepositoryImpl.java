package com.haru.doyak.harudoyak.repository.querydsl.impl;

import com.haru.doyak.harudoyak.entity.Member;

import static com.haru.doyak.harudoyak.entity.QMember.member;
import static com.haru.doyak.harudoyak.entity.QLevel.level;
import static com.haru.doyak.harudoyak.entity.QFile.file;

import com.haru.doyak.harudoyak.repository.querydsl.MemberCustomRepository;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Null;
import lombok.RequiredArgsConstructor;

import javax.swing.text.html.Option;
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
        System.out.println("Queried Member: " + getMember);  // 로그 추가
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
    public Optional<Member> findMemberByProviderId(String providerId) {
        Member getMember = jpaQueryFactory.selectFrom(member)
                .where(member.providerId.eq(providerId))
                .fetchOne();
        return Optional.ofNullable(getMember);
    }

    @Override
    public Tuple findLevelAndFileByMemberId(Long memberId) {
        return jpaQueryFactory.select(member, level, file)
                .from(member)
                .leftJoin(level).on(member.memberId.eq(level.memberId))
                .leftJoin(file).on(member.fileId.eq(file.fileId))
                .where(member.memberId.eq(memberId))
                .fetchOne();
    }

    @Override
    public Optional<Tuple> findMemberFileByMemberId(Long memberId) {
        Tuple tuple = jpaQueryFactory.select(member, file)
                .from(member)
                .leftJoin(file).on(member.fileId.eq(file.fileId))
                .where(member.memberId.eq(memberId))
                .fetchOne();
        return Optional.ofNullable(tuple);
    }
}
