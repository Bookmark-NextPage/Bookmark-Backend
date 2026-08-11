package com.example.bookmark.domain.bucketBoard.repository;

import com.example.bookmark.domain.bucketBoard.entity.BoardTheme;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BoardThemeRepository extends JpaRepository<BoardTheme, Long> {

    Optional<BoardTheme> findByBoardThemeId(Long id);
}
