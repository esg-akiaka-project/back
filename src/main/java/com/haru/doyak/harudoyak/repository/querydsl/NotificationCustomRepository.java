package com.haru.doyak.harudoyak.repository.querydsl;

import com.haru.doyak.harudoyak.entity.Notification;

import java.util.List;

public interface NotificationCustomRepository {
    List<Notification> findAllByMemberIdAndCategory(Long memberId, String category);
}
