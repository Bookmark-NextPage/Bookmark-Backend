package com.example.bookmark.domain.record.repository;

import com.example.bookmark.domain.record.entity.RecordLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RecordLikeRepository extends JpaRepository<RecordLike, Long> {
    boolean existsByRecordIdAndUserId(Long recordId, Long userId);
    Optional<RecordLike> findByRecordIdAndUserId(Long recordId, Long userId);
}