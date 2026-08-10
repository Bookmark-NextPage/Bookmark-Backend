package com.example.bookmark.domain.record.repository;

import com.example.bookmark.domain.record.entity.Record;
import com.example.bookmark.domain.record.entity.enums.RecordStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RecordRepository extends JpaRepository<Record, Long> {

    // User FK 기반 최근 DRAFT 조회
    Optional<Record> findFirstByUserIdAndStatusOrderByCreatedAtDesc(Long userId, RecordStatus status);
}