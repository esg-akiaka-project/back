package com.haru.doyak.harudoyak.domain.notification;

import com.haru.doyak.harudoyak.entity.Member;
import com.haru.doyak.harudoyak.repository.MemberRepository;
import com.querydsl.core.Tuple;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Component
public class LetterBatch {
    private final MemberRepository memberRepository;
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
        LocalDate localDate = LocalDate.now();
        System.out.println(localDate);
        // 피드백 도착 시간으로 검색해서 알림전송
        List<Tuple> tuples;
        notificationService.customNotify(1L, "aaaa", "도약이의 편지가 도착했어요.", "daily");
    }


    // 매 주 월요일 7시에 알림
    @Scheduled(cron = "0 0 7 * * MON")
    public void sendWeekFeedback() {
        LocalDate lastDate = LocalDate.now().minusDays(1);
        LocalDate firstDate = lastDate.minusDays(6);
//        List<Member> members = memberRepository.findAll();
//        // 금주에 도약기록 작성한 유저만 뽑기
//        for(Member member : members) {
//            notificationService.customNotify(member.getMemberId(), null, "금주 피드백을 확인해보세요.", "week");
//
//        }
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
