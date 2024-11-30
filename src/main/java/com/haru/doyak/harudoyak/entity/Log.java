package com.haru.doyak.harudoyak.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Log {
    // 도약기록 엔티티
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long logId;                 // 도약기록 아이디

    @ManyToOne
    @JoinColumn(name = "memberId")
    private Member member;              // 회원 아이디 (외래키)

    @OneToOne
    @JoinColumn(name = "fileId")
    private File file;                  // 파일 아이디 (외래키)

    @OneToMany(mappedBy = "log")
    private List<LogTag> tags;          // 로그&태그 복합키 엔티티 사용

    private String content;             // 도약기록 내용
    
    private String emotion;             // 오늘의 감정
    
    private LocalDateTime creationDate; // 도약기록 작성일

    @PrePersist
    private void prePersist() {
        this.creationDate = LocalDateTime.now();
    }

    @Builder
    public Log(Member member, String content, String emotion, LocalDateTime creationDate, Boolean isRead, File file) {
        this.member = member;
        this.content = content;
        this.emotion = emotion;
        this.creationDate = creationDate;
        this.file = file;
    }

}
