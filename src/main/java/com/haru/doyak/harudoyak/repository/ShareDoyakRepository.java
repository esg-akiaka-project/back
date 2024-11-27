package com.haru.doyak.harudoyak.repository;

import com.haru.doyak.harudoyak.entity.ShareDoyak;
import com.haru.doyak.harudoyak.repository.querydsl.ShareDoyakCustomRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShareDoyakRepository extends JpaRepository<ShareDoyak, Long>, ShareDoyakCustomRepository {

    Optional<ShareDoyak> findShareDoyakByShareDoyakId(Long shareDoyakId);
    boolean existsByshareDoyakId(Long shareDoyakId);

}
