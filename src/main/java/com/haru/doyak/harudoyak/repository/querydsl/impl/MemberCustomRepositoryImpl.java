package com.haru.doyak.harudoyak.repository.querydsl.impl;

import com.haru.doyak.harudoyak.dto.auth.LoginResDTO;
import com.haru.doyak.harudoyak.dto.file.FileDTO;
import com.haru.doyak.harudoyak.dto.member.LevelDTO;
import com.haru.doyak.harudoyak.dto.member.MemberDTO;
import com.haru.doyak.harudoyak.entity.File;
import com.haru.doyak.harudoyak.entity.Member;

import static com.haru.doyak.harudoyak.entity.QMember.member;
import static com.haru.doyak.harudoyak.entity.QLevel.level;
import static com.haru.doyak.harudoyak.entity.QFile.file;

import com.haru.doyak.harudoyak.repository.querydsl.MemberCustomRepository;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
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
    public Optional<LoginResDTO> findLevelAndFileByMemberId(Long memberId) {
        LoginResDTO loginResDTO = jpaQueryFactory
                .select(Projections.constructor(
                        LoginResDTO.class,
                                Projections.constructor(
                                        MemberDTO.class,
                                        member.memberId,
                                        member.email,
                                        member.nickname,
                                        member.aiNickname,
                                        member.goalName,
                                        member.isVerified,
                                        member.refreshToken
                                ),
                                Projections.constructor(
                                        LevelDTO.class,
                                        level.recentContinuity,
                                        level.maxContinuity,
                                        level.point,
                                        level.logCount,
                                        level.shareDoyakCount,
                                        level.firstDate,
                                        level.logLastDate
                                ),
                                Projections.constructor(
                                        FileDTO.class,
                                        file.fileId,
                                        file.filePathName,
                                        file.originalName
                                )
                        )
                )
                .from(member)
                .leftJoin(level).on(member.memberId.eq(level.memberId))
                .leftJoin(member.file, file)
                .where(member.memberId.eq(memberId))
                .fetchOne();
        return Optional.ofNullable(loginResDTO);
    }

    /**
     * oneToOne은 lazy로 member에 file을 넣느라 select를 한번 더 하는데, fetchJion을 하면 한번에 됨
     * @param memberId
     * @return
     */
    @Override
    public Optional<Member> findFileByMemberId(Long memberId) {
        Member getMember = jpaQueryFactory.select(member)
                .from(member)
                .leftJoin(member.file, file).fetchJoin()
                .where(member.memberId.eq(memberId))
                .fetchOne();
        return Optional.ofNullable(getMember);
    }
}
