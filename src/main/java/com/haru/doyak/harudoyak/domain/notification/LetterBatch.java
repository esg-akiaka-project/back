package com.haru.doyak.harudoyak.domain.notification;

import com.haru.doyak.harudoyak.entity.Member;
import static com.haru.doyak.harudoyak.entity.QMember.member;
import static com.haru.doyak.harudoyak.entity.QLog.log;
import static com.haru.doyak.harudoyak.entity.QLetter.letter;
import com.haru.doyak.harudoyak.repository.LogRepository;
import com.haru.doyak.harudoyak.repository.MemberRepository;
import com.querydsl.core.Tuple;
import lombok.RequiredArgsConstructor;
import org.springframework.cglib.core.Local;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Component
public class LetterBatch {
    private final MemberRepository memberRepository;
    private final LogRepository logRepository;
    private final NotificationService notificationService;
    // 임시 편지 리스트
    private final Map<Long, List<String>> userLetters = new HashMap<>();

    public void addLetterForUser(Long userId, String letterContent, String message) {
        userLetters.computeIfAbsent(userId, k -> new ArrayList<>()).add(letterContent);
        notificationService.customNotify(userId, userLetters.get(userId),message, "letter");
    }

    // 매일 7am 실행 -> 하루도약작성일 다음 날이면 알림
    @Scheduled(cron = "0 * * * * *")
//    @Scheduled(cron = "0 0 7 * * *")
    public void sendDailyFeedback(){
        LocalDate today = LocalDate.now();
        LocalDateTime startDateTime = today.minusDays(1).atTime(0, 0, 0, 0);// 어제 00:00
        LocalDateTime endDateTime = startDateTime.plusDays(1).minusNanos(1);// 어제 23:59
        System.out.println("daily"+startDateTime +" ~ "+endDateTime);// 2024-11-11T00:00 ~ 2024-11-11T23:59:59.999999999
        System.out.println("===========배치 실행============");
        // 피드백 도착 시간으로 검색해서 알림전송
        List<Tuple> tuples = logRepository.findLetterMemberWhereBetweenLogCreationDateTime(startDateTime, endDateTime);
        for(Tuple tuple : tuples){
            System.out.println(tuple);
            notificationService.customNotify(
                    tuple.get(member.memberId),
                    tuple.get(member.aiNickname)+"의 편지가 도착했어요.",
                    tuple.get(member.memberId)+"의 편지 알림 완료",
                    "daily");
        }
    }

    // 매 주 월요일 7시에 알림
    @Scheduled(cron = "0 * * * * *")
//    @Scheduled(cron = "0 0 7 * * MON")
    public void sendWeekFeedback() {
        LocalDateTime lastDateTime = LocalDate.now().atTime(0, 0, 0).minusSeconds(1);// 지난주 일요일 23:59
        LocalDateTime firstDateTime = LocalDate.now().atTime(0, 0, 0).minusDays(7);// 지난주 월 00:00:00
        System.out.println("week"+firstDateTime +" ~ "+lastDateTime);// week2024-11-05T00:00 ~ 2024-11-11T23:59:59

        List<Tuple> tuples = logRepository.findLogMemberWhereBetweenLogCreationDatetime(firstDateTime, lastDateTime);
        // 금주에 도약기록 작성한 유저만 뽑기
        for(Tuple tuple : tuples) {
            System.out.println(tuple);
            notificationService.customNotify(
                    tuple.get(member.memberId),
                    "지난 주 "+tuple.get(log.member.memberId.count())+"개의 피드백이 있습니다.",
                    tuple.get(member.memberId)+"의 지난 주 피드백 알림 완료.",
                    "week");
        }
    }
    
    // 매 월 1일 7시에 알림
    @Scheduled(cron = "0 * * * * *")
//    @Scheduled(cron = "0 0 7 1 * ?")
    public void sendMonthFeedback() {
        LocalDate lastMonth = LocalDate.now().minusMonths(1);
        LocalDateTime firstDateTime = LocalDate.
                of(lastMonth.getYear(), lastMonth.getMonth(), 1)
                .atTime(0, 0, 0);// 지난달 1일 00:00
        LocalDateTime lastDateTime = firstDateTime.plusMonths(1).minusNanos(1);// 지난달 30/31일 23:59
        System.out.println("month"+firstDateTime +" ~ "+lastDateTime);// month2024-10-01T00:00 ~ 2024-10-31T23:59:59.999999999

        List<Tuple> tuples = logRepository.findLogMemberWhereBetweenLogCreationDatetime(firstDateTime, lastDateTime);

        for(Tuple tuple : tuples) {
            System.out.println(tuple);
            notificationService.customNotify(
                    tuple.get(member.memberId),
                    "지난 달 "+tuple.get(log.member.memberId.count())+"개의 피드백이 있습니다.",
                    tuple.get(member.memberId)+"의 지난 달 피드백 알림 완료.",
                    "month");
        }
    }
}
