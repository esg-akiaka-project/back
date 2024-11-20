package com.haru.doyak.harudoyak.repository;

import com.haru.doyak.harudoyak.entity.LogTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LogTagRepository extends JpaRepository<LogTag, Long> {
}
