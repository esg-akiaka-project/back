package com.haru.doyak.harudoyak.repository;

import com.haru.doyak.harudoyak.domain.notification.SseEventName;
import com.haru.doyak.harudoyak.entity.Notification;
import com.haru.doyak.harudoyak.repository.querydsl.NotificationCustomRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long>, NotificationCustomRepository {
    List<Notification> findAllByMemberIdAndCategory(Long memberId, String category);
}
