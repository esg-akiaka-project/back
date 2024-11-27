package com.haru.doyak.harudoyak.repository;

import com.haru.doyak.harudoyak.entity.Notification;
import com.haru.doyak.harudoyak.repository.querydsl.NotificationCustomRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long>, NotificationCustomRepository {
}
