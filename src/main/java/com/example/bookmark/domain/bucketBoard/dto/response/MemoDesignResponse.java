package com.example.bookmark.domain.bucketBoard.dto.response;

import com.example.bookmark.domain.bucketBoard.entity.MemoDesign;

public record MemoDesignResponse(
        Long designId,
        String memoImageUrl
) {
    public static MemoDesignResponse from(MemoDesign design) {
        return new MemoDesignResponse(
                design.getMemoDesignId(),
                design.getMemoImageUrl()
        );
    }
}
