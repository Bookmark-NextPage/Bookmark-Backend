package com.example.bookmark.domain.record.repository;

import com.example.bookmark.domain.record.entity.RecordAiLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface RecordAiLogRepository extends JpaRepository<RecordAiLog, Long> {

    // 특정 유저의 하루(시작~끝) AI 호출 횟수 카운트
    long countByUserIdAndCreatedAtBetween(Long userId, LocalDateTime start, LocalDateTime end);
}