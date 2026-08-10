package com.example.bookmark.domain.collectBook.repository;

import com.example.bookmark.domain.collectBook.entity.CollectBook;
import com.example.bookmark.domain.collectBook.entity.enums.CollectBookType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CollectBookRepository extends JpaRepository<CollectBook, Long> {

    // 특정 유저의 콜렉트북 목록 조회 (연도 내림차순 정렬)
    List<CollectBook> findAllByUserIdOrderByYearDesc(Long userId);

    // TODO: 유저 조건 연결 예정
    // 유저 조건 없이 특정 연도의 [SYSTEM] 자동 생성 콜렉트북 조회
    Optional<CollectBook> findByYearAndCollectBookType(Integer year, CollectBookType collectBookType);
}