package com.example.bookmark.domain.bucketBoard.repository;

import com.example.bookmark.domain.bucketBoard.entity.MemoDesign;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemoDesignRepository extends JpaRepository<MemoDesign, Long> {

    Optional<MemoDesign> findFirstByBoardTheme_BoardThemeIdOrderByMemoDesignIdAsc(
            Long boardThemeId
    );

}
