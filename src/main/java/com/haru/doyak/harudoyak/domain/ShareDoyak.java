package com.haru.doyak.harudoyak.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.antlr.v4.runtime.misc.NotNull;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ShareDoyak {
    // 서로도약 엔티티

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long shDyId;    // 서로도약pk

                            // 회원 아이디(외래키)

    @NotNull
    private String title;   // 서로도약 제목

    @NotNull
    private String content; // 서로도약 글내용

    @CreationTimestamp
    private Date regDt;     // 서로도약 글등록일

}
