package com.example.bookmark.domain.bucketBoard.repository;

import com.example.bookmark.domain.bucketBoard.entity.BoardTheme;
import com.example.bookmark.domain.bucketBoard.entity.BucketBoardMemo;
import com.example.bookmark.domain.bucketBoard.entity.MemoCategory;
import com.example.bookmark.domain.bucketBoard.entity.enums.MemoState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BucketBoardMemoRepository extends JpaRepository<BucketBoardMemo, Long> {

    @Query("""
        select distinct m from BucketBoardMemo m
        join fetch m.memoCategory
        join fetch m.memoDesign
        where m.user.id = :userId and m.state = :state
        """)
    List<BucketBoardMemo> findAllByUserIdWithDetails(@Param("userId") Long userId, @Param("state") MemoState status);

    Optional<BucketBoardMemo> findByUserId(Long userId);

    @Query("""
            select m from BucketBoardMemo m
            join fetch m.memoCategory c
            join fetch m.memoDesign
            where m.user.id = :userId and c.memoCategoryId = :categoryId and m.state = :state
            """)
    List<BucketBoardMemo> findAllByUserIdAndCategoryWithDetails(
            @Param("userId") Long userId,
            @Param("state") MemoState status,
            @Param("categoryId") Long categoryId);


    @Query("""
    select m
    from BucketBoardMemo m
    join m.memoDesign d
    where m.user.id = :userId
      and m.state = :state
      and d.boardTheme.boardThemeId = :boardThemeId
    """)
    List<BucketBoardMemo> findAllByUserIdAndStateAndBoardThemeId(
            @Param("userId") Long userId,
            @Param("state") MemoState state,
            @Param("boardThemeId") Long boardThemeId
    );

    long countByUser_IdAndState(Long userId, MemoState state);

    long countByUser_IdAndStateAndUpdatedAtGreaterThanEqual(
            Long userId,
            MemoState state,
            LocalDateTime updatedAt
    );
//    @Query("""
//    select distinct m
//    from BucketBoardMemo m
//    join fetch m.memoCategory
//    join fetch m.memoDesign
//    where m.user.id = :userId
//      and m.state = :state
//    """)
//    List<BucketBoardMemo> findAllByUserIdAndStateWithDetails(
//            @Param("userId") Long userId,
//            @Param("state") MemoState state
//    );

}