package com.haru.doyak.harudoyak.dto.notification;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class WeekMonthNotificationDTO {
    private Long memberId;
    private Long count;
}
