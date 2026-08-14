package com.example.bookmark.domain.bucketBoard.dto.response;

import com.example.bookmark.domain.bucketBoard.entity.BucketBoardMemo;

public record BucketMoveResponse(
        Long bucketId,
        Double xPos,
        Double yPos
) {
    public static BucketMoveResponse of(Long bucketId, Double xPos, Double yPos) {
        return new BucketMoveResponse(
                bucketId,
                xPos,
                yPos
        );
    }
}
