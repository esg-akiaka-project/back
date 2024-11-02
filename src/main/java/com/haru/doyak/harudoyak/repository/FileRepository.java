package com.haru.doyak.harudoyak.repository;

import com.haru.doyak.harudoyak.entitys.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileRepository extends JpaRepository<File, Long> {

    File findByfilePathName(String filePathName);
    boolean existsByFileId(Long fileId);

}