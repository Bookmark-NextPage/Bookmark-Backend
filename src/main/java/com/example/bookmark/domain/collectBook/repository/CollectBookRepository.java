package com.example.bookmark.domain.collectBook.repository;

import com.example.bookmark.domain.collectBook.entity.CollectBook;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CollectBookRepository extends JpaRepository<CollectBook, Long> {
}