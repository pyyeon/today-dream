package com.springboot.like.repository;

import com.springboot.dream.entity.Dream;
import com.springboot.like.entity.Like;
import com.springboot.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {
    Optional<Like> findByDreamAndMember(Dream dream, Member member);
}
