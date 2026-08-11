package com.example.bookmark.domain.bucketBoard.dto.response;

import com.example.bookmark.domain.bucketBoard.entity.BoardTheme;

import java.util.List;

public record BoardThemesResponse(
        Long selectedBoardThemeId,
        List<ThemeResponse> boardThemes
) {
    public static BoardThemesResponse of(
            Long selectedBoardThemeId,
            List<BoardTheme> boardThemes
    ) {
        return new BoardThemesResponse(
                selectedBoardThemeId,
                boardThemes.stream()
                        .map(ThemeResponse::from)
                        .toList()
        );
    }
}
