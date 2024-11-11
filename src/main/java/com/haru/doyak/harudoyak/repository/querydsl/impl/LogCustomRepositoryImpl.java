package com.haru.doyak.harudoyak.repository.querydsl.impl;

import com.haru.doyak.harudoyak.dto.log.ResLogDTO;
import static com.haru.doyak.harudoyak.entity.QLog.log;
import static com.haru.doyak.harudoyak.entity.QMember.member;
import static com.haru.doyak.harudoyak.entity.QLetter.letter;
import com.haru.doyak.harudoyak.repository.querydsl.LogCustomRepository;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class LogCustomRepositoryImpl implements LogCustomRepository {
    private final JPAQueryFactory jpaQueryFactory;

    // private final QLog log = QLog.log;

    @Override
    public List<ResLogDTO> findLogAllByMemberId(Long memberId) {
        List<ResLogDTO> resLogDTOs = jpaQueryFactory
                .select(Projections.bean(ResLogDTO.class,
                        log.logId,
                        log.creationDate
                ))
                .from(log)
                .where(log.member.memberId.eq(memberId))
                .fetch();
        return resLogDTOs;
    }


    @Override
    public List<Tuple> findLogLetterMemberWhereArrivedDateBetweenDatetime(LocalDateTime startDate, LocalDateTime endDate) {
         return jpaQueryFactory.select(log, letter, member)
                .from(log)
                .leftJoin(member)
                .on(member.memberId.eq(log.member.memberId))
                .leftJoin(letter)
                .on(letter.log.logId.eq(log.logId))
                .where(
                        letter.arrivedDate.between(startDate, endDate)
                )
                .fetch();
    }

    public List<Tuple> findLogMemberWhereCreationDateBetweenDatetime(LocalDateTime startDate, LocalDateTime endDate) {
        return jpaQueryFactory.select(member, member.count())
                .from(log)
                .leftJoin(member)
                .on(member.memberId.eq(log.member.memberId))
                .where(
                        log.creationDate.between(startDate, endDate)
                )
                .groupBy(member.memberId)
                .fetch();
    }

}
