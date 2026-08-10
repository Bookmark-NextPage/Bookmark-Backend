package com.example.bookmark.domain.record.repository;

import com.example.bookmark.domain.record.entity.Keyword;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KeywordRepository extends JpaRepository<Keyword, Long> {
}