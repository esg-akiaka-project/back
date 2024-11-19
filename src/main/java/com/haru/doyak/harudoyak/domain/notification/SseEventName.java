package com.haru.doyak.harudoyak.domain.notification;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SseEventName {
    DAILY("daily"),
    WEEK("week"),
    MONTH("month"),
    POST_COMMENT("post_comment"),
    REPLY_COMMENT("reply_comment");

    private final String value;
}
