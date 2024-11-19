package com.haru.doyak.harudoyak.domain.notification;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SseEventName {
    DAILY("daily", "편지가 도착했어요"),
    WEEK("week", "저번주 우체통을 확인한세요"),
    MONTH("month", "저번달 우체통을 확인하세요"),
    POST_COMMENT("post_comment", "내 서로도약에 댓글이 달렸어요"),
    REPLY_COMMENT("reply_comment", "내 댓글에 답글이 달렸어요");

    private final String eventName;
    private final String title;
}
