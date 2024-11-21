package com.haru.doyak.harudoyak.repository;

import com.haru.doyak.harudoyak.entity.Log;
import com.haru.doyak.harudoyak.repository.querydsl.LogCustomRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LogRepository extends JpaRepository<Log, Long> , LogCustomRepository {

    Optional<Log> findLogByLogId(Long logId);

    boolean existsByLogId(Long logId);

}
