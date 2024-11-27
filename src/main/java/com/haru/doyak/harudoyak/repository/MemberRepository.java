package com.haru.doyak.harudoyak.repository;

import com.haru.doyak.harudoyak.entity.Member;
import com.haru.doyak.harudoyak.repository.querydsl.MemberCustomRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long>, MemberCustomRepository {

    Optional<Member> findMemberByMemberId(Long memberId);
    boolean existsByMemberId(Long memberId);
}
