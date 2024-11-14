package com.haru.doyak.harudoyak.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class File {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long fileId;         // 파일아이디

    @NotNull
    private String filePathName; // 파일경로+파일명

//    @NotNull
    private String originalName; // 업로드시파일명

    @Builder
    public File(String filePathName, String originalName) {
        this.filePathName = filePathName;
        this.originalName = originalName;
    }

    public void updateFilePathName(String newFileUrl) {
        this.filePathName = newFileUrl;
    }

}
