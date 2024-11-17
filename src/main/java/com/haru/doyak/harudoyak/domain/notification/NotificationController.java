package com.haru.doyak.harudoyak.domain.notification;

import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RequestMapping("api/notification")
@RestController
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final LetterBatch letterBatch;

    @GetMapping(value = "subscribe/{memberId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@PathVariable(value = "memberId") Long memberId) {
        return notificationService.subscribe(memberId);
    }

    @PostMapping("/add")
    public ResponseEntity add(@RequestParam("memberId")Long memberId, @RequestParam("content")String content){
        letterBatch.addLetterForUser(memberId, content, "도약이 편지가 도착했어요");
        return ResponseEntity.status(HttpStatus.OK).body("메세지 추가");
    }

}