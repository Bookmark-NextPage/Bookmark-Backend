package com.example.bookmark.domain.collectBook.dto.response;

import com.example.bookmark.domain.collectBook.entity.CollectBook;
import com.example.bookmark.domain.collectBook.entity.enums.BookColor;

public record CollectBookCreateResponse(
        Long collectBookId,
        String title,
        Integer year,
        BookColor bookColor
) {
    public static CollectBookCreateResponse from(CollectBook collectBook) {
        return new CollectBookCreateResponse(
                collectBook.getId(),
                collectBook.getTitle(),
                collectBook.getYear(),
                collectBook.getBookColor()
        );
    }
}