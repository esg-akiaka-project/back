package com.haru.doyak.harudoyak.domain.notification;

import com.haru.doyak.harudoyak.annotation.Authenticated;
import com.haru.doyak.harudoyak.entity.Notification;
import com.haru.doyak.harudoyak.exception.CustomException;
import com.haru.doyak.harudoyak.exception.ErrorCode;
import com.haru.doyak.harudoyak.security.AuthenticatedUser;
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
    public SseEmitter subscribe(@Authenticated AuthenticatedUser authenticatedUser,
                                @PathVariable(value = "memberId") Long memberId) {
        return notificationService.subscribe(authenticatedUser.getMemberId());
    }

    @PostMapping("add")
    public ResponseEntity add(@RequestParam("memberId")Long memberId, @RequestParam("content")String content){
        letterBatch.addLetterForUser(memberId, content, "도약이 편지가 도착했어요");
        return ResponseEntity.status(HttpStatus.OK).body("메세지 추가");
    }

    /**
     * @param memberId
     * @param category 알림 카테고리 [도약기록, 서로도약] = [log, post]
     * @return 알림 리스트
     */
    @GetMapping("{memberId}/list")
    public ResponseEntity getList(@Authenticated AuthenticatedUser authenticatedUser,
                                  @PathVariable("memberId") Long memberId,
                                  @RequestParam("category") String category)
    {
        if(category.equals("log")) {
            return ResponseEntity.ok().body(notificationService.getLogNotifications(authenticatedUser.getMemberId(), category));
        }
        if(category.equals("post")){
            return ResponseEntity.ok().body(notificationService.getLogNotifications(authenticatedUser.getMemberId(), category));
        }
        else throw new CustomException(ErrorCode.BAD_CATEGORY_NAME);
    }

}