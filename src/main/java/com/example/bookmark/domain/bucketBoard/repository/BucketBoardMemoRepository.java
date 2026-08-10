package com.example.bookmark.domain.bucketBoard.repository;

import com.example.bookmark.domain.bucketBoard.entity.BucketBoardMemo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BucketBoardMemoRepository extends JpaRepository<BucketBoardMemo, Long> {
}