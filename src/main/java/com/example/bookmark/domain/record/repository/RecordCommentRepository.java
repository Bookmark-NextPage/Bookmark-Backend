package com.example.bookmark.domain.record.repository;

import com.example.bookmark.domain.record.entity.RecordComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecordCommentRepository extends JpaRepository<RecordComment, Long> {
    List<RecordComment> findAllByRecordIdOrderByCreatedAtAsc(Long recordId);
}