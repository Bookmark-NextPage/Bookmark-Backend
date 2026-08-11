package com.example.bookmark.domain.record.repository;

import com.example.bookmark.domain.record.entity.Record;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RecordRepository extends JpaRepository<Record, Long> {
}