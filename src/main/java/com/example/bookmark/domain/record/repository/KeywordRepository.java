package com.example.bookmark.domain.record.repository;

import com.example.bookmark.domain.record.entity.Keyword;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface KeywordRepository extends JpaRepository<Keyword, Long> {

    // 특정 유저 소유의 키워드 목록만 안전하게 조회
    List<Keyword> findByIdInAndUserId(List<Long> ids, Long userId);
}