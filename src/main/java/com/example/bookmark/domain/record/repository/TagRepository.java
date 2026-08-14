package com.example.bookmark.domain.record.repository;

import com.example.bookmark.domain.record.entity.Keyword;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TagRepository extends JpaRepository<Keyword, Long> {

    // 특정 유저가 생성한 태그 개수 조회
    long countByUserId(Long userId);

    // 동일한 유저가 같은 이름의 태그를 중복 생성하는지 검증
    boolean existsByUserIdAndName(Long userId, String name);

    // 유저별 작성한 태그 목록 조회 (최신순)
    List<Keyword> findAllByUserIdOrderByIdDesc(Long userId);
}