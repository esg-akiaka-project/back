package com.haru.doyak.harudoyak.repository;

import com.haru.doyak.harudoyak.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long>, NotificationCustomRepository {
}
