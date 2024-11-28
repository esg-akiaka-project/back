package com.haru.doyak.harudoyak.domain.notification;

import com.haru.doyak.harudoyak.dto.notification.DailyNotificationDTO;
import com.haru.doyak.harudoyak.dto.notification.SseDataDTO;
import com.haru.doyak.harudoyak.dto.notification.WeekMonthNotificationDTO;
import com.haru.doyak.harudoyak.repository.LogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@RequiredArgsConstructor
@Component
@Slf4j
public class LetterBatch {
    private final LogRepository logRepository;
    private final NotificationService notificationService;

    // 매일 7am 실행 -> 하루도약작성일 다음 날이면 알림
//    @Scheduled(cron = "0 * * * * *")
//    @Scheduled(cron = "0 0 7 * * *")
    @Scheduled(cron = "0 33 11 * * *")
    public void sendDailyFeedback(){
        LocalDate today = LocalDate.now();
        LocalDateTime startDateTime = today.minusDays(1).atStartOfDay();// 어제 00:00
        LocalDateTime endDateTime = startDateTime.plusDays(1).minusNanos(1);// 어제 23:59
        log.info("DAILY"+startDateTime.toString()+"~"+ endDateTime.toString());
        // 도약기록 작성일로 검색해서 편지 가져오기
        List<DailyNotificationDTO> list = logRepository.findLetterMemberWhereBetweenLogCreationDateTime(startDateTime, endDateTime);
        for(DailyNotificationDTO dto : list){
            log.info(dto.toString());
            Long memberId = dto.getMemberId();
            String sender = dto.getAiNickName();
            String content = dto.getContent()
                    .substring(0, Math.min( dto.getContent().length(), 50)).concat("..");
            Long logId = dto.getLogId();

            SseDataDTO sseDataDTO = SseDataDTO.builder()
                    .sender(sender)
                    .content(content)
                    .logId(logId)
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
        LocalDate startOfThisWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate startOfLastWeek = startOfThisWeek.minusWeeks(1);
        LocalDate endOfLastWeek = startOfThisWeek.minusDays(1);

        LocalDateTime startDateTime = startOfLastWeek.atStartOfDay();
        LocalDateTime endDateTime = setLastTime(endOfLastWeek);

        // 저번주 도약기록 작성한 유저만 뽑기
        List<WeekMonthNotificationDTO> list = logRepository.findLogMemberWhereBetweenLogCreationDatetime(startDateTime, endDateTime);
        for(WeekMonthNotificationDTO dto : list) {
            Long memberId = dto.getMemberId();
            Long count = dto.getCount();

            SseDataDTO sseDataDTO = SseDataDTO.builder()
                    .count(count)
                    .startDate(startOfLastWeek.toString())
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
        List<WeekMonthNotificationDTO> list = logRepository.findLogMemberWhereBetweenLogCreationDatetime(startDateTime, endDateTime);
        for(WeekMonthNotificationDTO dto : list) {
            Long memberId = dto.getMemberId();
            Long count = dto.getCount();

            SseDataDTO sseDataDTO = SseDataDTO.builder()
                    .count(count)
                    .startDate(startOfLastMonth.toString())
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
