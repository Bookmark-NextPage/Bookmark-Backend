package com.example.bookmark.domain.bucketBoard.dto.response;

import com.example.bookmark.domain.bucketBoard.entity.BucketBoardMemo;
import com.example.bookmark.domain.bucketBoard.entity.MemoCategory;
import com.example.bookmark.domain.bucketBoard.entity.enums.MemoState;

import java.time.LocalDateTime;

public record MemoResponse(
        Long memoId,
        String content,
        Long categoryId,
        String categoryName,
        Long designId,
        Double posX,
        Double posY,
        LocalDateTime updatedAt
) {
    public static MemoResponse from(BucketBoardMemo memo) {
        return new MemoResponse(
                memo.getBucketBoardMemoId(),
                memo.getContent(),
                memo.getMemoCategory().getMemoCategoryId(),
                memo.getMemoCategory().getCategoryName(),
                memo.getMemoDesign().getMemoDesignId(),
                memo.getX(),
                memo.getY(),
                memo.getUpdatedAt()
        );
    }
}
