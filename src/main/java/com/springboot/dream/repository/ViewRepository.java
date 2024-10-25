package com.springboot.dream.repository;

import com.springboot.dream.entity.Dream;
import com.springboot.dream.entity.View;
import com.springboot.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ViewRepository extends JpaRepository<View, Long> {
    Optional<View> findByDreamAndMember(Dream dream, Member member);
}
