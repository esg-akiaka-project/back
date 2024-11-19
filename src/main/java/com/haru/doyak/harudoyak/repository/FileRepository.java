package com.haru.doyak.harudoyak.repository;

import com.haru.doyak.harudoyak.entity.File;
import com.haru.doyak.harudoyak.repository.querydsl.FileCustomRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface FileRepository extends JpaRepository<File, Long>, FileCustomRepository {

    Optional<File> findByFileId(Long fileId);
    boolean existsFileByFileId(Long fileId);

}
