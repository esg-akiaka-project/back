package com.haru.doyak.harudoyak.domain.notification;

import com.haru.doyak.harudoyak.entity.Notification;
import com.haru.doyak.harudoyak.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final EmitterRepository emitterRepository;
    private final NotificationRepository notificationRepository;


    private static final Long DEFAULT_TIMEOUT = 600L * 1000 * 60;

    public SseEmitter subscribe(Long memberId) {
        SseEmitter emitter = createEmitter(memberId);

        sendToClient(memberId, "EventStream Created. [memberId="+ memberId + "]", "sse 접속 성공");
        return emitter;
    }

    public <T> void customNotify(Long memberId, T data, String comment, String type) {
        sendToClient(memberId, data, comment, type);
    }
    public void notify(Long memberId, Object data, String comment) {
        sendToClient(memberId, data, comment);
    }

    private void sendToClient(Long memberId, Object data, String comment) {
        SseEmitter emitter = emitterRepository.get(memberId);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event()
                        .id(String.valueOf(memberId))
                        .name("sse")
                        .data(data)
                        .comment(comment));
            } catch (IOException e) {
                emitterRepository.deleteById(memberId);
                emitter.completeWithError(e);
            }
        }
    }


    private <T> void sendToClient(Long memberId, T data, String comment, String type) {
        SseEmitter emitter = emitterRepository.get(memberId);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event()
                        .id(String.valueOf(memberId))
                        .name(type)
                        .data(data)
                        .comment(comment));
            } catch (IOException e) {
                emitterRepository.deleteById(memberId);
                emitter.completeWithError(e);
            }
        }
    }

    private void saveNotification(Long memberId, String title, String content, SseEventName sseEventName) {
        Notification notification = Notification.builder()
                .title(title)
                .content(content)
                .sseEventName(sseEventName)
                .memberId(memberId)
                .build();
        notificationRepository.save(notification);
    }

    private SseEmitter createEmitter(Long memberId) {
        SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);
        emitterRepository.save(memberId, emitter);

        emitter.onCompletion(() -> emitterRepository.deleteById(memberId));
        emitter.onTimeout(() -> emitterRepository.deleteById(memberId));

        return emitter;
    }

//    private User validUser(Long memberId) {
//        return memberRepository.findById(memberId).orElseThrow(() -> new CustomException(UserErrorCode.NOT_FOUND_USER));
//    }
}
