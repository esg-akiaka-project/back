package com.haru.doyak.harudoyak.repository.querydsl.impl;

import com.haru.doyak.harudoyak.dto.log.EmotionDTO;
import com.haru.doyak.harudoyak.dto.log.ResLetterDTO;
import com.haru.doyak.harudoyak.dto.log.ResLogDTO;
import com.haru.doyak.harudoyak.dto.log.ResTagDTO;
import com.haru.doyak.harudoyak.repository.querydsl.LogCustomRepository;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.ConstantImpl;
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
    * 월간 도약기록 상세 조회
    * */
    // 태그 월간 집계
    @Override
    public List<ResTagDTO.TagMontlyDTO> findMontlyTagAll(Long memberId, LocalDateTime creationDate){
        List<ResTagDTO.TagMontlyDTO> tagMontlyDTOS = jpaQueryFactory
                .select(Projections.bean(
                        ResTagDTO.TagMontlyDTO.class,
                        tag.name.as("tagName"),
                        Expressions.numberTemplate(Long.class, "ROW_NUMBER() OVER (PARTITION BY {0}, DATE_FORMAT({1}, '%Y-%u') ORDER BY COUNT({2}) DESC)", log.member.memberId, creationDate, tag.name).as("rn")
                ))
                .from(log)
                .join(logTag).on(log.logId.eq(logTag.log.logId))
                .join(tag).on(logTag.tag.tagId.eq(tag.tagId))
                .where(log.member.memberId.eq(memberId))
                .groupBy(log.member.memberId, tag.name/*, montly*/)
                .orderBy(/*montly.asc()*/tag.tagId.count().desc())
                .limit(10L)
                .fetch();
        return tagMontlyDTOS;
    }

    // 감정 월간 집계
    @Override
    public List<EmotionDTO> findMontlyEmotion(Long memberId, LocalDateTime creationDate){
        List<EmotionDTO> emotionDTOS = jpaQueryFactory
                .select(Projections.bean(
                        EmotionDTO.class,
                        log.emotion,
                        log.emotion.count().as("emotionCount"),
                        Expressions.numberTemplate(Long.class, "ROW_NUMBER() OVER (PARTITION BY {0}, DATE_FORMAT({1}, '%Y-%u') ORDER BY COUNT({2}) DESC)", log.member.memberId, creationDate, log.emotion).as("rn")
                ))
                .from(log)
                .where(log.member.memberId.eq(memberId))
                .groupBy(log.member.memberId, log.emotion)
                /*.orderBy()*/
                .limit(3L)
                .fetch();

        return emotionDTOS;
    }

    // 도약이편지 Count
    @Override
    public List<ResLetterDTO.LetterMontlyDTO> findMontlyLetterAll(Long memberId, LocalDateTime creationDate){

        DateTemplate<LocalDateTime> montly = Expressions.dateTemplate(
                LocalDateTime.class,
                "DATE_FORMAT({0})",
                creationDate, ConstantImpl.create("%Y-%m")
        );

        List<ResLetterDTO.LetterMontlyDTO> letterMontlyDTOS = jpaQueryFactory
                .select(Projections.bean(
                        ResLetterDTO.LetterMontlyDTO.class,
                        /*montly.as("montly"),*/
                        letter.letterId.count().as("aiFeedbackCount")
                ))
                .from(log)
                .leftJoin(letter).on(log.logId.eq(letter.log.logId))
                .where(log.member.memberId.eq(memberId))
                /*.groupBy(montly.as("montly"))
                .orderBy(montly.as("montly").desc())*/
                .fetch();


        return letterMontlyDTOS;
    }

    /*
    * 주간 도약기록 상세 조회
    * */
    // 도약이편지 목록
    @Override
    public List<ResLetterDTO.LetterWeeklyDTO> findLetterByDate(Long memberId, LocalDateTime creationDate){
        // 월요일 날짜 계산
        LocalDateTime mondayDate = creationDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        // 일요일 날짜 계산
        LocalDateTime sundayDate = creationDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

        DateTemplate<LocalDateTime> monday = Expressions.dateTemplate(
                LocalDateTime.class, "{0}", mondayDate);
        /*DateTemplate<LocalDateTime> monday = Expressions.dateTemplate(
                LocalDateTime.class,
                "DATE_FORMAT(ADDDATE({0}, - WEEKDAY({0})+0), '%Y-%m-%d')",
                mondayDate
        );*/
        DateTemplate<LocalDateTime> sunday = Expressions.dateTemplate(
                LocalDateTime.class, "{0}", sundayDate);
        /*DateTemplate<LocalDateTime> sunday = Expressions.dateTemplate(
                LocalDateTime.class,
                "DATE_FORMAT(ADDDATE({0}, - WEEKDAY({0})+6), '%Y-%m-%d')",
                sundayDate
        );*/
        List<ResLetterDTO.LetterWeeklyDTO> letterWeeklyDTOS = jpaQueryFactory
                .select(Projections.bean(
                        ResLetterDTO.LetterWeeklyDTO.class,
                        monday.as("monday"),
                        sunday.as("sunday"),
                        letter.arrivedDate.as("feedBackDate"),
                        letter.content.as("feedback")
                ))
                .from(log)
                .join(member).on(log.member.memberId.eq(member.memberId))
                .join(letter).on(log.logId.eq(letter.log.logId))
                .where(log.member.memberId.eq(memberId))
                .groupBy(letter.letterId/*,monday, sunday*/)
                .orderBy(monday.asc())
                .fetch();

        return letterWeeklyDTOS;
    }

    // 감정 주간 집계
    @Override
    public List<EmotionDTO> findEmotionByDate(Long memberId, LocalDateTime creationDate){
        // 월요일 날짜 계산
        LocalDateTime mondayDate = creationDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        // 일요일 날짜 계산
        LocalDateTime sundayDate = creationDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
/*        DateTemplate<LocalDateTime> monday = Expressions.dateTemplate(
                LocalDateTime.class, "DATE_FORMAT(ADDDATE({0}, - WEEKDAY({0})+0), '%Y-%m-%d')", creationDate);*/
        DateTemplate<LocalDateTime> monday = Expressions.dateTemplate(
                LocalDateTime.class, "{0}", mondayDate);

/*        DateTemplate<LocalDateTime> sunday = Expressions.dateTemplate(
                LocalDateTime.class, "DATE_FORMAT(ADDDATE({0}, - WEEKDAY({0})+6), '%Y-%m-%d')", creationDate);*/
        DateTemplate<LocalDateTime> sunday = Expressions.dateTemplate(
                LocalDateTime.class, "{0}", sundayDate);

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

    // 태그 주간 집계
    @Override
    public List<ResTagDTO.TagWeeklyDTO> findTagsByName(Long memberId, LocalDateTime creationDate){
        // 월요일 날짜 계산
        LocalDateTime mondayDate = creationDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        // 일요일 날짜 계산
        LocalDateTime sundayDate = creationDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
/*        DateTemplate<LocalDateTime> monday = Expressions.dateTemplate(
                LocalDateTime.class, "DATE_FORMAT(ADDDATE({0}, - WEEKDAY({0})+0), '%Y-%m-%d')", creationDate);*/
        DateTemplate<LocalDateTime> monday = Expressions.dateTemplate(
                LocalDateTime.class, "{0}", mondayDate);

/*        DateTemplate<LocalDateTime> sunday = Expressions.dateTemplate(
                LocalDateTime.class, "DATE_FORMAT(ADDDATE({0}, - WEEKDAY({0})+6), '%Y-%m-%d')", creationDate);*/
        DateTemplate<LocalDateTime> sunday = Expressions.dateTemplate(
                LocalDateTime.class, "{0}", sundayDate);
        List<ResTagDTO.TagWeeklyDTO> tagWeeklyDTOS = jpaQueryFactory
                .select(Projections.bean(
                        ResTagDTO.TagWeeklyDTO.class,
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
                /*.having(Expressions.numberTemplate(Long.class, "rn").loe(10))*/
                .orderBy(monday.asc())
                .fetch();
        return tagWeeklyDTOS;
    }

    /*
    * 일간 도약기록 상세 조회
    * */
    @Override
    public List<ResLogDTO.ResDailyLogDTO> findLogByLogIdAndMemberId(Long memberId, Long logId){
        List<ResLogDTO.ResDailyLogDTO> resDailyLogDTOS = jpaQueryFactory
                .select(Projections.bean(
                        ResLogDTO.ResDailyLogDTO.class,
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

        List<ResTagDTO> resTagDTOS = jpaQueryFactory
                .select(Projections.bean(
                        ResTagDTO.class,
                        tag.name.as("tagName")
                ))
                .from(logTag)
                .join(tag).on(logTag.tag.tagId.eq(tag.tagId))
                .where(logTag.log.logId.eq(logId))
                .fetch();

        resDailyLogDTOS.forEach(resDailyLogDTO -> {
            resDailyLogDTO.setTagNameList(resTagDTOS);
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


    /**
     * @param startDate startDate 이상
     * @param endDate endDate 이하
     * @return <List> 기간 내에 작성한 로그의 member, letter
     */
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

    /**
     * @param startDate
     * @param endDate
     * @return <List> 기간 내에 로그 작성한 member, log count
     */
    @Override
    public List<Tuple> findLogMemberWhereBetweenLogCreationDatetime(LocalDateTime startDate, LocalDateTime endDate) {
        return jpaQueryFactory.select(member.memberId, log.member.memberId.count())
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
