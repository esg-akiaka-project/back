package com.haru.doyak.harudoyak.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;

@AllArgsConstructor
@Builder
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Level {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long levelId;

    @JoinColumn
    private Long memberId;          // 회원 아이디(외래키)

    private Long recentContinuity;  // 최근연속 작성일
    private Long maxContinuity;     // 최대연속일
    private Long point;             // 경험치
    private Long logCount;          // daily doyak count
    private Long shareDoyakCount;  // share doyak count
    @CreationTimestamp
    private LocalDate firstDate;
    private LocalDate logLastDate;

    @PrePersist
    public void prePersist() {
        if(point == null) this.point = 0L;
        if(maxContinuity == null) this.maxContinuity = 0L;
        if(recentContinuity == null) this.recentContinuity = 0L;
        if(logCount == null) this.logCount = 0L;
        if(shareDoyakCount == null) this.shareDoyakCount = 0L;
    }

    public void updateWhenPostLog() {
        // log count+1, 포인트, 최근연속일, 최대연속일, 최신 작성일
        this.logCount += 1;
        this.point += 10;
        if(this.logLastDate!=null && this.logLastDate.isEqual(LocalDate.now().minusDays(1))) {
            this.recentContinuity += 1;
            this.maxContinuity = Math.max(maxContinuity, recentContinuity);
        }else {
            this.recentContinuity = 1L;// 최근 첫 작성
            this.maxContinuity = Math.max(maxContinuity, recentContinuity);
        }
        this.logLastDate = LocalDate.now();
    }

    public void updateWhenPostShareDoyak() {
        // share count+1, 포인트
        this.shareDoyakCount += 1;
        this.point += 5;
    }
}
