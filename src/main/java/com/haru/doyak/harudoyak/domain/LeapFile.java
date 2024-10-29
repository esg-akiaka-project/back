package com.haru.doyak.harudoyak.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.antlr.v4.runtime.misc.NotNull;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LeapFile {
    // 서러도약파일 엔티티

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long fileId;     // 서로도약 파일아이디

                             // 서로도약 아이디(외래키)

    @NotNull
    private String fileName; // 저장된 파일명

    @NotNull
    private String oriName;  // 업로드시 파일명

}