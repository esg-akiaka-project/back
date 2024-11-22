package com.haru.doyak.harudoyak.repository.querydsl;

import com.haru.doyak.harudoyak.entity.Doyak;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DoyakRepository extends JpaRepository<Doyak, Long>, DoyakCustomRepository {
}
