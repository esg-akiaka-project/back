package com.haru.doyak.harudoyak.domain.notification;

import com.haru.doyak.harudoyak.entity.Member;
import static com.haru.doyak.harudoyak.entity.QMember.member;
import static com.haru.doyak.harudoyak.entity.QLog.log;
import static com.haru.doyak.harudoyak.entity.QLetter.letter;
import com.haru.doyak.harudoyak.repository.LogRepository;
import com.haru.doyak.harudoyak.repository.MemberRepository;
import com.querydsl.core.Tuple;
import lombok.RequiredArgsConstructor;
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

    // 매 시간 실행 -> 피드백 도착 시간과 일치하면 알림
    @Scheduled(cron = "0 0 1 * * *")
    public void sendDailyFeedback(){
        LocalDate today = LocalDate.now();
        LocalDateTime startDateTime = today.atTime(0, 0, 0, 0);
        LocalDateTime endDateTime = startDateTime.plusHours(1).minusSeconds(1);
        // 피드백 도착 시간으로 검색해서 알림전송
        List<Tuple> tuples = logRepository.findLogLetterMemberWhereArrivedDateBetweenDatetime(startDateTime, endDateTime);
        for(Tuple tuple : tuples){
            notificationService.customNotify(
                    tuple.get(member).getMemberId(),
                    tuple.get(letter).getContent(),
                    tuple.get(member).getAiNickname()+"의 편지가 도착했어요.",
                    "daily");
        }
    }


    // 매 주 월요일 7시에 알림
    @Scheduled(cron = "0 0 7 * * MON")
    public void sendWeekFeedback() {
        LocalDateTime lastDate = LocalDate.now().atTime(0, 0, 0).minusSeconds(1);// 지난주 일요일 23:59:59
        LocalDateTime firstDate = LocalDate.now().atTime(0, 0, 0).minusDays(7);// 지난주 월 00:00:00
        List<Tuple> tuples = logRepository.findLogMemberWhereCreationDateBetweenDatetime(firstDate, lastDate);
        // 금주에 도약기록 작성한 유저만 뽑기
        for(Tuple tuple : tuples) {
            notificationService.customNotify(
                    tuple.get(member).getMemberId(),
                    tuple.get(member.count()),
                    "금주 피드백을 확인해보세요.",
                    "week");
        }
    }

    // 매 월 1일 7시에 알림
    @Scheduled(cron = "0 0 7 1 * ?")
    public void sendMonthFeedback() {
        LocalDate searchMonth = LocalDate.now().minusMonths(1);
//        List<Member> members = memberRepository.findAll();
//        // 금월에 도약기록 작성한 유저만 뽑기
//        for(Member member : members) {
//            notificationService.customNotify(member.getMemberId(), null, "이번 달 피드백을 확인해보세요.", "month");
//
//        }
    }
}
