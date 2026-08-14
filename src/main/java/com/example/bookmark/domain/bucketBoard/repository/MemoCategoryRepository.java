package com.example.bookmark.domain.bucketBoard.repository;

import com.example.bookmark.domain.bucketBoard.entity.MemoCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MemoCategoryRepository extends JpaRepository<MemoCategory, Long> {

    Optional<MemoCategory> findMemoCategoryByMemoCategoryId(Long categoryId);

    @Query("""
    select c from MemoCategory c
    where c.defaultCategory = true
       or c.user.id = :userId
    """)
    List<MemoCategory> findAvailableCategories(@Param("userId") Long userId);

    List<MemoCategory> findAllByUserId(Long userId);

    List<MemoCategory> findAllByDefaultCategory(Boolean defaultCategory);

    Boolean existsByCategoryNameAndUserId(String categoryName, Long userId);
    Boolean existsByCategoryNameAndDefaultCategory(String categoryName, Boolean defaultCategory);

}
