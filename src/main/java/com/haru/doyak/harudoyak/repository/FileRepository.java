package com.haru.doyak.harudoyak.repository;

import com.haru.doyak.harudoyak.entity.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FileRepository extends JpaRepository<File, Long> {

    Optional<File> findByFileId(Long fileId);
    boolean existsFileByFileId(Long fileId);

}
