package com.haru.doyak.harudoyak.repository.querydsl.impl;

import com.haru.doyak.harudoyak.dto.log.EmotionDTO;
import com.haru.doyak.harudoyak.dto.log.ResLetterDTO;
import com.haru.doyak.harudoyak.dto.log.ResLogDTO;
import com.haru.doyak.harudoyak.dto.log.ResTagDTO;
import com.haru.doyak.harudoyak.dto.notification.DailyNotificationDTO;
import com.haru.doyak.harudoyak.dto.notification.WeekMonthNotificationDTO;
import com.haru.doyak.harudoyak.repository.querydsl.LogCustomRepository;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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
    public Optional<List<ResTagDTO.TagMonthlyDTO>> findMontlyTagAll(Long memberId, LocalDateTime startMonthDayDate, LocalDateTime endMonthDayDate){

        List<ResTagDTO.TagMonthlyDTO> tagMonthlyDTOS = jpaQueryFactory
                .select(Projections.bean(
                        ResTagDTO.TagMonthlyDTO.class,
                        tag.name.as("tagName"),
                        Expressions.numberTemplate(Long.class, "ROW_NUMBER() OVER (PARTITION BY {0}, DATE_FORMAT({1}, '%Y-%u') ORDER BY COUNT({2}) DESC)", log.member.memberId, log.creationDate, tag.name).as("rn")
                ))
                .from(log)
                .join(logTag).on(log.logId.eq(logTag.log.logId))
                .join(tag).on(logTag.tag.tagId.eq(tag.tagId))
                .where(log.member.memberId.eq(memberId), log.creationDate.between(startMonthDayDate, endMonthDayDate))
                .groupBy(log.member.memberId, tag.name)
                .orderBy(tag.name.count().desc())
                .limit(10)
                .fetch();
        return Optional.ofNullable(tagMonthlyDTOS);

    }

    // 감정 월간 집계
    @Override
    public Optional<List<EmotionDTO.ResEmotionMonthlyDTO>> findMontlyEmotion(Long memberId, LocalDateTime startMonthDayDate, LocalDateTime endMonthDayDate){

        List<EmotionDTO.ResEmotionMonthlyDTO> emotionDTOS = jpaQueryFactory
                .select(Projections.bean(
                        EmotionDTO.ResEmotionMonthlyDTO.class,
                        log.emotion,
                        log.emotion.count().as("emotionCount"),
                        Expressions.numberTemplate(Long.class, "ROW_NUMBER() OVER (PARTITION BY {0}, DATE_FORMAT({1}, '%Y-%u') ORDER BY COUNT({2}) DESC)", log.member.memberId, log.creationDate, log.emotion).as("rn")
                ))
                .from(log)
                .where(log.member.memberId.eq(memberId), log.creationDate.between(startMonthDayDate, endMonthDayDate))
                .groupBy(log.member.memberId, log.emotion)
                .orderBy(log.emotion.count().desc())
                .limit(3)
                .fetch();

        return Optional.ofNullable(emotionDTOS);

    }

    // 도약이편지 Count
    @Override
    public Optional<List<ResLetterDTO.LetterMonthlyDTO>> findMontlyLetterAll(Long memberId, LocalDateTime startMonthDayDate, LocalDateTime endMonthDayDate){

        List<ResLetterDTO.LetterMonthlyDTO> letterMonthlyDTOS = jpaQueryFactory
                .select(Projections.bean(
                        ResLetterDTO.LetterMonthlyDTO.class,
                        letter.letterId.count().as("aiFeedbackCount")
                ))
                .from(log)
                .leftJoin(letter).on(log.logId.eq(letter.log.logId))
                .where(log.member.memberId.eq(memberId), log.creationDate.between(startMonthDayDate, endMonthDayDate))
                .fetch();


        return Optional.ofNullable(letterMonthlyDTOS);

    }

    /*
    * 주간 도약기록 상세 조회
    * */
    // 도약이편지 목록
    @Override
    public Optional<List<ResLetterDTO.LetterWeeklyDTO>> findLetterByDate(Long memberId, LocalDateTime mondayDate, LocalDateTime sundayDate){

        List<ResLetterDTO.LetterWeeklyDTO> letterWeeklyDTOS = jpaQueryFactory
                .select(Projections.bean(
                        ResLetterDTO.LetterWeeklyDTO.class,
                        letter.creationDate.as("feedBackDate"),
                        letter.content.as("feedback")
                ))
                .from(log)
                .join(member).on(log.member.memberId.eq(member.memberId))
                .join(letter).on(log.logId.eq(letter.log.logId))
                .where(log.member.memberId.eq(memberId), log.creationDate.between(mondayDate, sundayDate))
                .orderBy(log.creationDate.asc())
                .fetch();

        return Optional.ofNullable(letterWeeklyDTOS);

    }

    // 감정 주간 집계
    @Override
    public Optional<List<EmotionDTO>> findEmotionByDate(Long memberId, LocalDateTime mondayDate, LocalDateTime sundayDate){

        List<EmotionDTO> emotionDTOS = jpaQueryFactory
                .select(Projections.bean(
                        EmotionDTO.class,
                        log.emotion,
                        log.emotion.count().as("emotionCount"),
                        Expressions.numberTemplate(Long.class, "ROW_NUMBER() OVER (PARTITION BY {0}, DATE_FORMAT({1}, '%Y-%u') ORDER BY COUNT({2}) DESC)", log.member.memberId, log.creationDate, log.emotion).as("rn")
                ))
                .from(log)
                .where(log.member.memberId.eq(memberId), log.creationDate.between(mondayDate, sundayDate))
                .groupBy(log.member.memberId, log.emotion)
                .orderBy(log.emotion.count().desc())
                .limit(3)
                .fetch();

        return Optional.ofNullable(emotionDTOS);

    }

    // 태그 주간 집계
    @Override
    public Optional<List<ResTagDTO.TagWeeklyDTO>> findTagsByName(Long memberId, LocalDateTime mondayDate, LocalDateTime sundayDate){

        List<ResTagDTO.TagWeeklyDTO> tagWeeklyDTOS = jpaQueryFactory
                .select(Projections.bean(
                        ResTagDTO.TagWeeklyDTO.class,
                        tag.name.as("tagName"),
                        tag.name.count().as("tagCount"),
                        Expressions.numberTemplate(Long.class, "ROW_NUMBER() OVER (PARTITION BY {0}, DATE_FORMAT({1}, '%Y-%u') ORDER BY COUNT({2}) DESC)", log.member.memberId, log.creationDate, tag.name).as("rn")
                ))
                .from(log)
                .leftJoin(logTag).on(log.logId.eq(logTag.logTagId.logId))
                .leftJoin(tag).on(logTag.logTagId.tagId.eq(tag.tagId))
                .where(log.member.memberId.eq(memberId), log.creationDate.between(mondayDate, sundayDate))
                .groupBy(tag.name)
                .orderBy(tag.name.count().desc())
                .limit(10)
                .fetch();

        return Optional.ofNullable(tagWeeklyDTOS);

    }

    /*
    * 일간 도약기록 상세 조회
    * */
    @Override
    public Optional<List<ResLogDTO.ResDailyLogDTO>> findLogByLogIdAndMemberId(Long memberId, Long logId){

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
                                .otherwise("도약이 답변이 아직 없습니다.")
                                .as("letterContent"),
                        new CaseBuilder()
                                .when(letter.creationDate.isNotNull())
                                .then(letter.creationDate)
                                .otherwise(log.creationDate)
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

        return Optional.ofNullable(resDailyLogDTOS);

    }

    /*
    * 해당 회원이 작성한 도약기록 목록 조회
    * @param : meberId(Long)
    * */
    @Override
    public Optional<List<ResLogDTO>> findLogAllByMemberId(Long memberId) {
        List<ResLogDTO> resLogDTOs = jpaQueryFactory
                .select(Projections.bean(ResLogDTO.class,
                        log.logId,
                        log.creationDate
                ))
                .from(log)
                .where(log.member.memberId.eq(memberId))
                .fetch();

        return Optional.ofNullable(resLogDTOs);
    }


    /**
     * @param startDate startDate 이상
     * @param endDate endDate 이하
     * @return <List> 기간 내에 작성한 로그의 member, letter
     */
    @Override
    public List<DailyNotificationDTO> findLetterMemberWhereBetweenLogCreationDateTime(LocalDateTime startDate, LocalDateTime endDate) {
         return jpaQueryFactory.select(
                         Projections.constructor(
                                 DailyNotificationDTO.class,
                                 letter.content,
                                 member.memberId,
                                 member.aiNickname,
                                 log.logId
                         )
                 )
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
    public List<WeekMonthNotificationDTO> findLogMemberWhereBetweenLogCreationDatetime(LocalDateTime startDate, LocalDateTime endDate) {
        return jpaQueryFactory.select(
                Projections.constructor(
                        WeekMonthNotificationDTO.class,
                        member.memberId,
                        log.member.memberId.count()
                )
                )
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
