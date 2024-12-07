package com.haru.doyak.harudoyak.repository.querydsl.impl;

import com.haru.doyak.harudoyak.domain.notification.SseEventName;
import com.haru.doyak.harudoyak.entity.Notification;
import static com.haru.doyak.harudoyak.entity.QNotification.notification;
import com.haru.doyak.harudoyak.repository.querydsl.NotificationCustomRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class NotificationCustomRepositoryImpl implements NotificationCustomRepository {
    private final JPAQueryFactory jpaQueryFactory;

    /**
     * @param memberId
     * @param category
     * @return 알림 최신순 정렬 조회
     */
    public List<Notification> findAllByMemberIdAndCategory(Long memberId, String category) {
        return jpaQueryFactory.selectFrom(notification)
                .where(
                        notification.memberId.eq(memberId)
                                .and(notification.category.eq(category))
                )
                .orderBy(notification.creationDate.desc())
                .fetch();
    }
}
