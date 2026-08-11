package com.example.bookmark.domain.record.repository;

import com.example.bookmark.domain.record.entity.Record;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecordRepository extends JpaRepository<Record, Long> {
    // 챕터 ID 목록에 포함된 모든 기록을 ID 내림차순(최신순)으로 조회
    List<Record> findAllByChapterIdInOrderByIdDesc(List<Long> chapterIds);}