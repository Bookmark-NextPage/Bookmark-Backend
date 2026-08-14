package com.example.bookmark.domain.bucketBoard.repository;

import com.example.bookmark.domain.bucketBoard.entity.BoardTheme;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface BoardThemeRepository extends JpaRepository<BoardTheme, Long> {

    Optional<BoardTheme> findByBoardThemeId(Long id);

    @Query("""
        select distinct bt
        from BoardTheme bt
        left join fetch bt.memoDesigns
        order by bt.boardThemeId
        """)
    List<BoardTheme> findAllWithMemoDesigns();
}
