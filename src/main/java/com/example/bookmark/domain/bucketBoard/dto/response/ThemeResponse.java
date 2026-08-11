package com.example.bookmark.domain.bucketBoard.dto.response;

import com.example.bookmark.domain.bucketBoard.entity.BoardTheme;

import java.util.Comparator;
import java.util.List;

public record ThemeResponse(
        Long themeId,
        String themeName,
        String themeImageUrl,
        String font,
        List<MemoDesignResponse> designs
) {
    public static ThemeResponse from(BoardTheme theme) {
        return new ThemeResponse(
                theme.getBoardThemeId(),
                theme.getThemeName(),
                theme.getThemeImageUrl(),
                theme.getFont(),
                theme.getMemoDesigns().stream()
                        .map(MemoDesignResponse::from)
                        .toList()
        );
    }
}
