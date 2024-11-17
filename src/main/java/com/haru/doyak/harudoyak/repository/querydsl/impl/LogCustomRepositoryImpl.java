package com.haru.doyak.harudoyak.repository.querydsl.impl;

import com.haru.doyak.harudoyak.dto.log.*;
import com.haru.doyak.harudoyak.dto.log.ResLogDTO;

import com.haru.doyak.harudoyak.dto.log.ResDailyLogDTO;
import com.haru.doyak.harudoyak.dto.log.TagDTO;
import com.haru.doyak.harudoyak.repository.querydsl.LogCustomRepository;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.DateTemplate;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.time.LocalDateTime;
import java.util.List;

import static com.haru.doyak.harudoyak.entity.QFile.file;
import static com.haru.doyak.harudoyak.entity.QLetter.letter;
import static com.haru.doyak.harudoyak.entity.QLog.log;
import static com.haru.doyak.harudoyak.entity.QLogTag.logTag;
import static com.haru.doyak.harudoyak.entity.QMember.member;
import static com.haru.doyak.harudoyak.entity.QTag.tag;

@Repository
@RequiredArgsConstructor
public class LogCustomRepositoryImpl implements LogCustomRepository {
    private final JPAQueryFactory jpaQueryFactory;

    /*
    * 주간 도약기록 상세 조회
    * */
    // 도약이편지 목록
    @Override
    public List<LetterWeeklyDTO> findLetterByDate(Long memberId, LocalDateTime creationDate){
        // 월요일 날짜 계산
        LocalDateTime mondayDate = creationDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDateTime sundayDate = creationDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));

/*        DateTemplate<LocalDateTime> monday = Expressions.dateTemplate(
                LocalDateTime.class, "DATE_FORMAT(ADDDATE({0}, - WEEKDAY({0})+0), '%Y-%m-%d')", creationDate);*/
        DateTemplate<LocalDateTime> monday = Expressions.dateTemplate(
                LocalDateTime.class, "DATE_FORMAT({0}, '%Y-%m-%d')", mondayDate);
/*        DateTemplate<LocalDateTime> sunday = Expressions.dateTemplate(
                LocalDateTime.class, "DATE_FORMAT(ADDDATE({0}, - WEEKDAY({0})+6), '%Y-%m-%d')", creationDate);*/
        DateTemplate<LocalDateTime> sunday = Expressions.dateTemplate(
                LocalDateTime.class, "DATE_FORMAT({0}, '%Y-%m-%d')", sundayDate);
        List<LetterWeeklyDTO> letterWeeklyDTOS = jpaQueryFactory
                .select(Projections.bean(
                        LetterWeeklyDTO.class,
                        monday.as("monday"),
                        sunday.as("sunday"),
                        letter.arrivedDate.as("feedBackDate"),
                        letter.content.as("feedback")
                ))
                .from(log)
                .join(member).on(log.member.memberId.eq(member.memberId))
                .join(letter).on(log.logId.eq(letter.log.logId))
                .where(member.memberId.eq(memberId))
                .groupBy(member.memberId, monday, sunday)
                .orderBy(monday.asc())
                .fetch();

        return letterWeeklyDTOS;
    }

    // 감정 집계
    @Override
    public List<EmotionDTO> findEmotionByDate(Long memberId, LocalDateTime creationDate){
        DateTemplate<LocalDateTime> monday = Expressions.dateTemplate(
                LocalDateTime.class, "DATE_FORMAT(ADDDATE({0}, - WEEKDAY({0})+0), '%Y-%m-%d')", creationDate);
        DateTemplate<LocalDateTime> sunday = Expressions.dateTemplate(
                LocalDateTime.class, "DATE_FORMAT(ADDDATE({0}, - WEEKDAY({0})+6), '%Y-%m-%d')", creationDate);
        List<EmotionDTO> emotionDTOS = jpaQueryFactory
                .select(Projections.bean(
                        EmotionDTO.class,
                        log.emotion,
                        log.emotion.count().as("emotionCount"),
                        monday.as("monday"),
                        sunday.as("sunday"),
                        Expressions.numberTemplate(Long.class, "ROW_NUMBER() OVER (PARTITION BY {0}, DATE_FORMAT({1}, '%Y-%u') ORDER BY COUNT({2}) DESC)", log.member.memberId, creationDate, log.emotion).as("rn")
                ))
                .from(log)
                .where(log.member.memberId.eq(memberId))
                .groupBy(log.member.memberId, monday, log.emotion)
                .orderBy(monday.asc())
                .fetch();
        return emotionDTOS;
    }

    // 태그 집계
    @Override
    public List<TagWeeklyDTO> findTagsByName(Long memberId, LocalDateTime creationDate){
        DateTemplate<LocalDateTime> monday = Expressions.dateTemplate(
                LocalDateTime.class, "DATE_FORMAT(ADDDATE({0}, - WEEKDAY({0})+0), '%Y-%m-%d')", creationDate);
        DateTemplate<LocalDateTime> sunday = Expressions.dateTemplate(
                LocalDateTime.class, "DATE_FORMAT(ADDDATE({0}, - WEEKDAY({0})+6), '%Y-%m-%d')", creationDate);
        List<TagWeeklyDTO> tagWeeklyDTOS = jpaQueryFactory
                .select(Projections.bean(
                        TagWeeklyDTO.class,
                        tag.name.as("tagName"),
                        tag.name.count().as("tagCount"),
                        monday.as("monday"),
                        sunday.as("sunday"),
                        Expressions.numberTemplate(Long.class, "ROW_NUMBER() OVER (PARTITION BY {0}, DATE_FORMAT({1}, '%Y-%u') ORDER BY COUNT({2}) DESC)", log.member.memberId, creationDate, tag.name).as("rn")
                ))
                .from(log)
                .leftJoin(logTag).on(log.logId.eq(logTag.logTagId.logId))
                .leftJoin(tag).on(logTag.logTagId.tagId.eq(tag.tagId))
                .where(log.member.memberId.eq(memberId))
                .groupBy(monday, sunday, tag.name)
                .having(Expressions.numberTemplate(Long.class, "rn").loe(10))
                .orderBy(monday.asc())
                .fetch();
        return tagWeeklyDTOS;
    }

    /*
    * 일간 도약기록 상세 조회
    * */
    @Override
    public List<ResDailyLogDTO> findLogByLogIdAndMemberId(Long memberId, Long logId){
        List<ResDailyLogDTO> resDailyLogDTOS = jpaQueryFactory
                .select(Projections.bean(
                        ResDailyLogDTO.class,
                        log.emotion,
                        log.content.as("logContent"),
                        file.filePathName.as("logImageUrl"),

                        // 도약이 편지가 null일시 CaseBuilder 사용해서 기본값 설정
                        new CaseBuilder()
                                .when(letter.content.isNotNull())
                                .then(letter.content)
                                .otherwise(letter.content)
                                .as("letterContent"),
                        new CaseBuilder()
                                .when(letter.arrivedDate.isNotNull())
                                .then(letter.arrivedDate)
                                .otherwise(letter.arrivedDate)
                                .as("letterCreationDate")
                ))
                .from(log)
                .join(member).on(log.member.memberId.eq(member.memberId))
                .leftJoin(file).on(log.file.fileId.eq(file.fileId))
                .leftJoin(letter).on(log.logId.eq(letter.log.logId))
                .where(member.memberId.eq(memberId).and(log.logId.eq(logId)))
                .groupBy(log.logId)
                .fetch();

        List<TagDTO> tagDTOS = jpaQueryFactory
                .select(Projections.bean(
                        TagDTO.class,
                        tag.name.as("tagName")
                ))
                .from(logTag)
                .join(tag).on(logTag.tag.tagId.eq(tag.tagId))
                .where(logTag.log.logId.eq(logId))
                .fetch();

        resDailyLogDTOS.forEach(resDailyLogDTO -> {
            resDailyLogDTO.setTagNameList(tagDTOS);
        });

        return resDailyLogDTOS;

    }

    /*
    * 해당 회원이 작성한 도약기록 목록 조회
    * @param : meberId(Long)
    * */
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
    public List<Tuple> findLetterMemberWhereBetweenLogCreationDateTime(LocalDateTime startDate, LocalDateTime endDate) {
         return jpaQueryFactory.select(letter.content, member.memberId, member.aiNickname)
                .from(log)
                .leftJoin(member)
                .on(member.memberId.eq(log.member.memberId))
                .rightJoin(letter)
                .on(letter.log.logId.eq(log.logId))
                .where(
                        log.creationDate.between(startDate, endDate)
                )
                .fetch();
    }

    @Override
    public List<Tuple> findLogMemberWhereBetweenLogCreationDatetime(LocalDateTime startDate, LocalDateTime endDate) {
        return jpaQueryFactory.select(log.member.memberId, log.member.memberId.count())
                .from(log)
                .leftJoin(member)
                .on(member.memberId.eq(log.member.memberId))
                .where(
                        log.creationDate.between(startDate, endDate)
                )
                .groupBy(log.member.memberId)
                .fetch();
    }

}
