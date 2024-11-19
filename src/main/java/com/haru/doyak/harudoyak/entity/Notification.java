package com.haru.doyak.harudoyak.entity;

import com.haru.doyak.harudoyak.domain.notification.SseEventName;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@AllArgsConstructor
@Builder
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long notificationId;
    @NotNull
    private String title;
    @NotNull
    private String content;
    @NotNull
    @Enumerated(EnumType.STRING)
    private SseEventName sseEventName;
    @NotNull
    private Boolean isRead;
    @CreatedDate
    private LocalDateTime creationDate;

    @JoinColumn
    private Long memberId;

    @PrePersist
    protected void prePersist() {
        if(this.isRead == null) this.isRead = false;
    }

}
