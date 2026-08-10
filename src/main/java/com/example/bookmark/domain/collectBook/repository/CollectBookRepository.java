package com.example.bookmark.domain.collectBook.repository;

import com.example.bookmark.domain.collectBook.entity.CollectBook;
import com.example.bookmark.domain.collectBook.entity.enums.CollectBookType;
import com.example.bookmark.domain.collectBook.entity.enums.Visibility;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CollectBookRepository extends JpaRepository<CollectBook, Long> {

    long countByUserId(Long userId);

    // 특정 유저의 콜렉트북 목록 조회 (연도 내림차순 정렬)
    List<CollectBook> findAllByUserIdOrderByYearDesc(Long userId);

    // 유저 조건 없이 특정 연도의 [SYSTEM] 자동 생성 콜렉트북 조회
    Optional<CollectBook> findByUserIdAndYearAndCollectBookType(Long userId, Integer year, CollectBookType collectBookType);

    // TODO: 추후 기록 생성 일자 기준으로 수정해야됨
    List<CollectBook> findTop5ByUserIdOrderByCreatedAtDesc(Long userId);

    // 공개 범위로 필터링한 최근 5개 TODO: 추후 기록 생성 일자 기준으로 수정해야됨
    List<CollectBook> findTop5ByUserIdAndVisibilityInOrderByCreatedAtDesc(
            Long userId, java.util.Collection<Visibility> visibilities);

    // 회원탈퇴 시 삭제
    void deleteAllByUserId(Long userId);
}