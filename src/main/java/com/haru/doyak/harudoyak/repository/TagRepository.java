package com.haru.doyak.harudoyak.repository;

import com.haru.doyak.harudoyak.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {



}
