package com.example.bookmark.domain.bucketBoard.dto.response;

public record CategoryResponse(
        Long categoryId,
        String categoryName
        ) {
    public static CategoryResponse of(Long categoryId, String categoryName) {
        return new CategoryResponse(
                categoryId,
                categoryName
        );
    }
}
