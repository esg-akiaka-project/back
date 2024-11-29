package com.haru.doyak.harudoyak.domain.notification;

import com.haru.doyak.harudoyak.dto.notification.SseDataDTO;
import com.haru.doyak.harudoyak.entity.Notification;
import com.haru.doyak.harudoyak.exception.CustomException;
import com.haru.doyak.harudoyak.exception.ErrorCode;
import com.haru.doyak.harudoyak.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {
    private final EmitterRepository emitterRepository;
    private final NotificationRepository notificationRepository;


    private static final Long DEFAULT_TIMEOUT = 600L * 1000 * 60;

    public SseEmitter subscribe(Long memberId) {
        SseEmitter emitter = createEmitter(memberId);

        sendToClient(memberId, "EventStream Created. [memberId="+ memberId + "]", "sse 접속 성공");
        return emitter;
    }

    /**
     * @param memberId 고유한 id, map으로 구분해서 없어도 괜찮음
     * @param data 클라이언트에 전달되는 데이터 내용
     * @param comment 클라이언트에선 주석처리됨 안보임
     * @param sseEventName 이벤트 이름
     */
    public void customNotify(Long memberId, SseDataDTO data, String comment, SseEventName sseEventName) {
        sendToClient(memberId, data, comment, sseEventName);
    }

    private void sendToClient(Long memberId, Object data, String comment) {
        SseEmitter emitter = emitterRepository.get(memberId);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event()
                        .id(String.valueOf(memberId))
                        .name("SSE")
                        .data(data)
                        .comment(comment));
            } catch (IOException e) {
                emitterRepository.deleteById(memberId);
                emitter.completeWithError(e);
            }
        }
    }

    private void sendToClient(Long memberId, SseDataDTO data, String comment, SseEventName sseEventName) {
        log.info("알림 저장");
        saveNotification(memberId, data, sseEventName);

        log.info("알림 전송 시작");
        SseEmitter emitter = emitterRepository.get(memberId);
        log.info(memberId+"에게 sse 전송 시도중");
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event()
                        .id(String.valueOf(memberId))
                        .name(sseEventName.name())
                        .data(data)
                        .comment(comment));
            } catch (IOException e) {
                emitterRepository.deleteById(memberId);
                emitter.completeWithError(e);
            }
        }
        log.info(memberId+"가 접속해있지 않음");
    }


    private SseEmitter createEmitter(Long memberId) {
        SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);
        emitterRepository.save(memberId, emitter);

        emitter.onCompletion(() -> emitterRepository.deleteById(memberId));
        emitter.onTimeout(() -> emitterRepository.deleteById(memberId));

        return emitter;
    }

    public List<Notification> getLogNotifications(Long memberId, String category) {
        return notificationRepository.findAllByMemberIdAndCategory(memberId, category);
    }

    public void saveNotification(Long memberId, SseDataDTO sseDataDTO, SseEventName sseEventName) {
        Notification notification = Notification.builder()
                .data(sseDataDTO)
                .sseEventName(sseEventName)
                .memberId(memberId)
                .build();
        notificationRepository.save(notification);
    }

    public Notification readNotification(Long memberId, Long notificationId) {
        Notification notification = notificationRepository
                .findById(notificationId).orElseThrow(()-> new CustomException(ErrorCode.NOTIFICATION_NOT_FOND));

        if(notification.getMemberId()!=memberId){
            throw new CustomException(ErrorCode.DIFFERENT_MEMBER_TOKEN);
        }

        if(!notification.getIsRead()) notification.read();
        return notificationRepository.save(notification);
    }
}
