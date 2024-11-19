package com.haru.doyak.harudoyak.repository;

import com.haru.doyak.harudoyak.entity.Letter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LetterRepository extends JpaRepository<Letter, Long> {
}
