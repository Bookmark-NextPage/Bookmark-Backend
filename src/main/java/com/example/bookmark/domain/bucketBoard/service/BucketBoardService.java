package com.example.bookmark.domain.bucketBoard.service;

import com.example.bookmark.common.exception.CustomException;
import com.example.bookmark.common.response.ErrorCode;
import com.example.bookmark.domain.bucketBoard.dto.response.BoardResponse;
import com.example.bookmark.domain.bucketBoard.entity.BoardTheme;
import com.example.bookmark.domain.bucketBoard.entity.BucketBoardMemo;
import com.example.bookmark.domain.bucketBoard.entity.MemoCategory;
import com.example.bookmark.domain.bucketBoard.entity.enums.MemoState;
import com.example.bookmark.domain.bucketBoard.exception.BucketBoardErrorCode;
import com.example.bookmark.domain.bucketBoard.repository.BucketBoardMemoRepository;
import com.example.bookmark.domain.bucketBoard.repository.MemoCategoryRepository;
import com.example.bookmark.domain.user.entity.User;
import com.example.bookmark.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BucketBoardService {

    private final UserRepository userRepository;
    private final BucketBoardMemoRepository bucketBoardMemoRepository;
    private final MemoCategoryRepository memoCategoryRepository;

    public BoardResponse getBoard(Long categoryId, Long userId) {
        User user  = userRepository.findById(userId).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        BoardTheme theme = user.getBoardTheme();
        if (theme == null) {
            throw new CustomException(BucketBoardErrorCode.THEME_NOT_FOUND);
        }

        List<BucketBoardMemo> memos;

        if (categoryId == null) {
            memos = bucketBoardMemoRepository.findAllByUserIdWithDetails(userId, MemoState.PLAN);
        } else {
            MemoCategory category = memoCategoryRepository.findById(categoryId)
                    .orElseThrow(() -> new CustomException(BucketBoardErrorCode.CATEGORY_NOT_FOUND));

            if (!Boolean.TRUE.equals(category.getDefaultCategory())
                    && (category.getUser() == null
                    || !category.getUser().getId().equals(userId))) {
                throw new CustomException(BucketBoardErrorCode.NOT_OWN_CATEGORY);
            }

            memos = bucketBoardMemoRepository.findAllByUserIdAndCategoryWithDetails(userId, MemoState.PLAN, categoryId);
        }

        return BoardResponse.of(theme, memos);


    }

}
