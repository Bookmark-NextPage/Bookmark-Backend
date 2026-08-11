package com.example.bookmark.domain.friend.dto.response;

import com.example.bookmark.domain.collectBook.entity.CollectBook;
import com.example.bookmark.domain.collectBook.entity.enums.BookColor;
import com.example.bookmark.domain.user.entity.User;

import java.util.List;

public record FriendPageResponse(
        Long userId,
        String name,
        String loginId,
        String bio,
        String profileImageUrl,
        List<PublicBook> publicCollectBooks
) {
    public record PublicBook(
            Long collectBookId,
            Integer year,
            String title,
            BookColor bookColor
    ) {
        public static PublicBook from(CollectBook book) {
            return new PublicBook(book.getId(), book.getYear(), book.getTitle(), book.getBookColor());
        }
    }

    public static FriendPageResponse of(User user, List<CollectBook> books) {
        List<PublicBook> bookDtos = books.stream().map(PublicBook::from).toList();
        return new FriendPageResponse(
                user.getId(), user.getName(), user.getLoginId(),
                user.getBio(), user.getProfileImageUrl(), bookDtos
        );
    }
}