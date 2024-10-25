package com.springboot.dream.repository;

import com.springboot.dream.entity.Dream;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DreamRepository extends JpaRepository<Dream, Long> {
    Page<Dream> findByDreamKeywords_NameContaining(String keyword, Pageable pageable);

    Page<Dream> findByDreamStatus(Dream.DreamStatus status, Pageable pageRequest);

    // 드림 상태가 ACTIVE인 드림을 필터링하고, 드림 키워드를 기반으로 검색하는 메소드
    // 키워드가 비어있으면 전체 드림을 반환
    Page<Dream> findByDreamStatusAndDreamSecretAndDreamKeywords_NameContaining(Dream.DreamStatus status, Dream.DreamSecret dreamSecret, String keyword, Pageable pageRequest);

    Page<Dream> findByDreamStatusAndDreamSecret(Dream.DreamStatus status, Dream.DreamSecret dreamSecret, Pageable pageRequest);

}
