package com.haru.doyak.harudoyak.dto.notification;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class DailyNotificationDTO {
    private String content;
    private Long memberId;
    private String aiNickName;
    private Long logId;
}
