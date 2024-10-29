package com.haru.doyak.harudoyak.entitys;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Level {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long levelId;

    @OneToOne
    @JoinColumn(name = "memId")
    private Member member; // 회원 아이디(외래키)

    private Long continuity;// 최근연속일
    private Long maxContinuity;// 최대연속일
    private Long point;
    private Long ddCount;// daily doyak count
    private Long sdCount;// share doyak count
}