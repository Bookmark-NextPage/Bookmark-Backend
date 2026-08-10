package com.example.bookmark.domain.record.repository;

import com.example.bookmark.domain.record.entity.Record;
import com.example.bookmark.domain.record.entity.enums.RecordStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RecordRepository extends JpaRepository<Record, Long> {

    // 가장 최근 DRAFT(임시저장) 기록 1건 조회 (생성일시 내림차순)
    Optional<Record> findFirstByStatusOrderByCreatedAtDesc(RecordStatus status);
}