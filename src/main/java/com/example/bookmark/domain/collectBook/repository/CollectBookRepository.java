package com.example.bookmark.domain.collectBook.repository;

import com.example.bookmark.domain.collectBook.entity.CollectBook;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CollectBookRepository extends JpaRepository<CollectBook, Long> {

    // 특정 유저의 콜렉트북 목록 조회 (연도 내림차순 정렬)
    List<CollectBook> findAllByUserIdOrderByYearDesc(Long userId);
}