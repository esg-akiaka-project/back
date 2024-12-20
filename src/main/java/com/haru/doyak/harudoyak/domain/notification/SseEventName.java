package com.haru.doyak.harudoyak.domain.notification;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SseEventName {
    DAILY("DAILY", "의 편지가 도착했어요", "log"),
    WEEK("WEEK", "저번주 우체통을 확인한세요", "log"),
    MONTH("MONTH", "저번달 우체통을 확인하세요", "log"),
    POST_COMMENT("POST_COMMENT", "내 서로도약에 댓글을 남겼어요", "post"),
    REPLY_COMMENT("REPLY_COMMENT", "내 댓글에 답글을 남겼어요", "post");

    private final String eventName;
    private final String title;
    private final String category;
}
