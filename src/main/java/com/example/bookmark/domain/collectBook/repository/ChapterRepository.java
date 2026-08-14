package com.example.bookmark.domain.collectBook.repository;

import com.example.bookmark.domain.collectBook.entity.Chapter;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChapterRepository extends JpaRepository<Chapter, Long> {}

