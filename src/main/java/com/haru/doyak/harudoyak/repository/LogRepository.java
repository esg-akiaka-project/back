package com.haru.doyak.harudoyak.repository;

import com.haru.doyak.harudoyak.entity.Log;
import com.haru.doyak.harudoyak.repository.querydsl.LogCustomRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LogRepository extends JpaRepository<Log, Long> , LogCustomRepository {

    Optional<Log> findLogByLogId(Long logId);

}
