package com.haru.doyak.harudoyak.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.haru.doyak.harudoyak.domain.notification.SseDataConverter;
import com.haru.doyak.harudoyak.domain.notification.SseDataDTO;
import com.haru.doyak.harudoyak.domain.notification.SseEventName;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

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
    @Column(columnDefinition = "json")
    @Convert(converter = SseDataConverter.class)
    private SseDataDTO data;

    @NotNull
    @Enumerated(EnumType.STRING)
    private SseEventName sseEventName;
    @NotNull
    private String category;
    @NotNull
    private Boolean isRead;
    @CreationTimestamp
    private LocalDateTime creationDate;

    @JsonIgnore
    @JoinColumn
    private Long memberId;

    @PrePersist
    protected void prePersist() {
        if(this.isRead == null){
            this.isRead = false;
        }
        if(this.category == null){
            this.category = this.sseEventName.getCategory();
        }
    }

}
