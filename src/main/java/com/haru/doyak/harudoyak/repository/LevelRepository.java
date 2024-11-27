package com.haru.doyak.harudoyak.repository;

import com.haru.doyak.harudoyak.entity.Level;
import com.haru.doyak.harudoyak.repository.querydsl.LevelCustomRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LevelRepository extends JpaRepository<Level, Long>, LevelCustomRepository {
}
