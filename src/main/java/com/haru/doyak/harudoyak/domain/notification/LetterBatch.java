package com.haru.doyak.harudoyak.domain.notification;

import static com.haru.doyak.harudoyak.entity.QMember.member;
import static com.haru.doyak.harudoyak.entity.QLog.log;
import static com.haru.doyak.harudoyak.entity.QLetter.letter;
import com.haru.doyak.harudoyak.repository.LogRepository;
import com.haru.doyak.harudoyak.repository.MemberRepository;
import com.querydsl.core.Tuple;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Component
public class LetterBatch {
    private final LogRepository logRepository;
    private final NotificationService notificationService;
    // 임시 편지 리스트
    private final Map<Long, List<String>> userLetters = new HashMap<>();

    public void addLetterForUser(Long memberId, String letterContent, String message) {
        userLetters.computeIfAbsent(memberId, k -> new ArrayList<>()).add(letterContent);
        notificationService.customNotify(memberId, userLetters.get(memberId),message, null);
        notificationService.saveNotification(
                memberId,
                SseDataDTO.builder()
                        .postTitle("test")
                        .content("content")
                        .sender("sender")
                        .build(),
                SseEventName.POST_COMMENT);
    }

    // 매일 7am 실행 -> 하루도약작성일 다음 날이면 알림
//    @Scheduled(cron = "0 * * * * *")
    @Scheduled(cron = "0 0 7 * * *")
    public void sendDailyFeedback(){
        LocalDate today = LocalDate.now();
        LocalDateTime startDateTime = today.minusDays(1).atStartOfDay();// 어제 00:00
        LocalDateTime endDateTime = startDateTime.plusDays(1).minusNanos(1);// 어제 23:59

        // 도약기록 작성일로 검색해서 편지 가져오기
        List<Tuple> tuples = logRepository.findLetterMemberWhereBetweenLogCreationDateTime(startDateTime, endDateTime);
        for(Tuple tuple : tuples){
            Long memberId = tuple.get(member.memberId);
            String sender = tuple.get(member.aiNickname);
            String content = tuple.get(letter.content)
                    .substring(0, Math.min( tuple.get(letter.content).length(), 20)).concat("...");

            SseDataDTO sseDataDTO = SseDataDTO.builder()
                    .sender(sender)
                    .content(content)
                    .build();
            // 알림 전송
            notificationService.customNotify(
                    memberId,
                    sseDataDTO,
                    "도약이 편지 알림",
                    SseEventName.DAILY);
        }
    }

    // 매 주 월요일 7시에 알림
//    @Scheduled(cron = "0 * * * * *")
    @Scheduled(cron = "0 0 7 * * MON")
    public void sendWeekFeedback() {
        LocalDate today = LocalDate.now();
        LocalDate startOfThisWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
        LocalDate startOfLastWeek = startOfThisWeek.minusWeeks(1);
        LocalDate endOfLastWeek = startOfThisWeek.minusDays(1);

        LocalDateTime startDateTime = startOfLastWeek.atStartOfDay();
        LocalDateTime endDateTime = setLastTime(endOfLastWeek);

        // 저번주 도약기록 작성한 유저만 뽑기
        List<Tuple> tuples = logRepository.findLogMemberWhereBetweenLogCreationDatetime(startDateTime, endDateTime);
        for(Tuple tuple : tuples) {
            Long memberId = tuple.get(member.memberId);
            Long count = tuple.get(log.member.memberId.count());

            SseDataDTO sseDataDTO = SseDataDTO.builder()
                    .count(count)
                    .build();

            // 알림 전송
            notificationService.customNotify(
                    memberId,
                    sseDataDTO,
                    "지난주 성장기록 알림",
                    SseEventName.WEEK);
        }
    }

    // 매 월 1일 7시에 알림
//    @Scheduled(cron = "0 * * * * *")
    @Scheduled(cron = "0 0 7 1 * ?")
    public void sendMonthFeedback() {
        LocalDate today = LocalDate.now();
        YearMonth lastMonth = YearMonth.from(today).minusMonths(1);
        LocalDate startOfLastMonth = lastMonth.atDay(1);
        LocalDate endOfLastMonth = lastMonth.atEndOfMonth();

        LocalDateTime startDateTime = startOfLastMonth.atStartOfDay();
        LocalDateTime endDateTime = setLastTime(endOfLastMonth);

        // 저번달 도약기록 작성한 유저 뽑기
        List<Tuple> tuples = logRepository.findLogMemberWhereBetweenLogCreationDatetime(startDateTime, endDateTime);
        for(Tuple tuple : tuples) {
            Long memberId = tuple.get(member.memberId);
            Long count = tuple.get(log.member.memberId.count());

            SseDataDTO sseDataDTO = SseDataDTO.builder()
                    .count(count)
                    .build();

            // 알림 전송
            notificationService.customNotify(
                    memberId,
                    sseDataDTO,
                    "저번달 성장기록 알림",
                    SseEventName.MONTH);
        }
    }

    public LocalDateTime setLastTime(LocalDate localDate){
        return localDate.atTime(23, 59, 59,999999999);
    }
}
