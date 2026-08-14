package com.example.bookmark.domain.bucketBoard.dto.response;

import com.example.bookmark.domain.bucketBoard.entity.BoardTheme;
import com.example.bookmark.domain.bucketBoard.entity.BucketBoardMemo;

import java.util.List;

public record BoardResponse(
        ThemeResponse theme,
        List<MemoResponse> memos
) {
    public static BoardResponse of(BoardTheme boardTheme, List<BucketBoardMemo> memos) {
        return new BoardResponse(
                ThemeResponse.from(boardTheme),
                memos.stream().map(MemoResponse::from).toList()
        );
    }
}
