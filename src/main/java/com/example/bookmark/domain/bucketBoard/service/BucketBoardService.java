package com.example.bookmark.domain.bucketBoard.service;

import com.example.bookmark.common.exception.CustomException;
import com.example.bookmark.common.response.ErrorCode;
import com.example.bookmark.domain.bucketBoard.dto.request.BucketMoveRequest;
import com.example.bookmark.domain.bucketBoard.dto.request.BucketWriteRequest;
import com.example.bookmark.domain.bucketBoard.dto.request.CategoryRequest;
import com.example.bookmark.domain.bucketBoard.dto.response.BoardResponse;
import com.example.bookmark.domain.bucketBoard.dto.response.BucketMoveResponse;
import com.example.bookmark.domain.bucketBoard.dto.response.CategoryResponse;
import com.example.bookmark.domain.bucketBoard.dto.response.MemoResponse;
import com.example.bookmark.domain.bucketBoard.entity.BoardTheme;
import com.example.bookmark.domain.bucketBoard.entity.BucketBoardMemo;
import com.example.bookmark.domain.bucketBoard.entity.MemoCategory;
import com.example.bookmark.domain.bucketBoard.entity.MemoDesign;
import com.example.bookmark.domain.bucketBoard.entity.enums.MemoState;
import com.example.bookmark.domain.bucketBoard.exception.BucketBoardErrorCode;
import com.example.bookmark.domain.bucketBoard.repository.BucketBoardMemoRepository;
import com.example.bookmark.domain.bucketBoard.repository.MemoCategoryRepository;
import com.example.bookmark.domain.bucketBoard.repository.MemoDesignRepository;
import com.example.bookmark.domain.user.entity.User;
import com.example.bookmark.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BucketBoardService {

    private final UserRepository userRepository;
    private final BucketBoardMemoRepository bucketBoardMemoRepository;
    private final MemoCategoryRepository memoCategoryRepository;
    private final MemoDesignRepository memoDesignRepository;

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

    @Transactional
    public MemoResponse createBucket(Long userId, BucketWriteRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        BoardTheme theme = user.getBoardTheme();
        if (theme == null) {
            throw new CustomException(BucketBoardErrorCode.THEME_NOT_FOUND);
        }

        MemoCategory category = memoCategoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new CustomException(BucketBoardErrorCode.CATEGORY_NOT_FOUND));

        // 공통 카테고리는 허용, 개인 카테고리는 생성자만 허용
        if (!Boolean.TRUE.equals(category.getDefaultCategory())
                && (category.getUser() == null
                || !category.getUser().getId().equals(userId))) {
            throw new CustomException(BucketBoardErrorCode.NOT_OWN_CATEGORY);
        }

        MemoDesign memoDesign = memoDesignRepository.findById(request.memoDesignId())
                .orElseThrow(() -> new CustomException(
                        BucketBoardErrorCode.MEMO_DESIGN_NOT_FOUND
                ));

        // 선택한 메모지가 현재 유저의 보드 테마에 속하는지 검증
        if (!memoDesign.getBoardTheme().getBoardThemeId()
                .equals(theme.getBoardThemeId())) {
            throw new CustomException(BucketBoardErrorCode.DESIGN_NOT_IN_THEME);
        }

        BucketBoardMemo memo = BucketBoardMemo.builder()
                .user(user)
                .memoCategory(category)
                .memoDesign(memoDesign)
                .content(request.content())
                .state(MemoState.PLAN)
                .scrapBook(false)
                .xPos(0.0)
                .yPos(0.0)
                .build();

        BucketBoardMemo savedMemo = bucketBoardMemoRepository.save(memo);

        return MemoResponse.from(savedMemo);
    }

    @Transactional
    public MemoResponse updateBucket(Long userId, Long bucketId, BucketWriteRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        BucketBoardMemo memo = bucketBoardMemoRepository.findById(bucketId)
                .orElseThrow(() -> new CustomException(
                        BucketBoardErrorCode.BUCKET_BOARD_NOT_FOUND
                ));

        // 본인 메모인지 확인
        if (!memo.getUser().getId().equals(user.getId())) {
            throw new CustomException(BucketBoardErrorCode.NOT_OWN_MEMO);
        }

        if(memo.getState() == MemoState.COMPLETE) {
            throw new CustomException(BucketBoardErrorCode.MEMO_ALREADY_COMPLETED);
        }

        MemoCategory category = memoCategoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new CustomException(BucketBoardErrorCode.CATEGORY_NOT_FOUND));

        // 공통 카테고리 또는 본인 개인 카테고리만 선택 가능
        if (!Boolean.TRUE.equals(category.getDefaultCategory())
                && (category.getUser() == null
                || !category.getUser().getId().equals(userId))) {
            throw new CustomException(BucketBoardErrorCode.NOT_OWN_CATEGORY);
        }

//        MemoDesign memoDesign = memoDesignRepository.findById(request.memoDesignId())
//                .orElseThrow(() -> new CustomException(BucketBoardErrorCode.MEMO_DESIGN_NOT_FOUND));
//
//        // 현재 유저 테마에 속한 메모지인지 확인
//        if (!memoDesign.getBoardTheme().getBoardThemeId()
//                .equals(memo.getUser().getBoardTheme().getBoardThemeId())) {
//            throw new CustomException(BucketBoardErrorCode.DESIGN_NOT_IN_THEME);
//        }

        memo.update(request.content(), category);

        return MemoResponse.from(memo);

    }

    @Transactional
    public Long deleteBucket(Long userId, Long bucketId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        BucketBoardMemo memo = bucketBoardMemoRepository.findById(bucketId)
                .orElseThrow(() -> new CustomException(
                        BucketBoardErrorCode.BUCKET_BOARD_NOT_FOUND
                ));

        // 본인 메모인지 확인
        if (!memo.getUser().getId().equals(user.getId())) {
            throw new CustomException(BucketBoardErrorCode.NOT_OWN_MEMO);
        }

        if(memo.getState() == MemoState.COMPLETE) {
            throw new CustomException(BucketBoardErrorCode.MEMO_ALREADY_COMPLETED);
        }

        bucketBoardMemoRepository.delete(memo);

        return bucketId;
    }

    @Transactional
    public Long completeBucket(Long userId, Long bucketId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        BucketBoardMemo memo = bucketBoardMemoRepository.findById(bucketId)
                .orElseThrow(() -> new CustomException(
                        BucketBoardErrorCode.BUCKET_BOARD_NOT_FOUND
                ));

        // 본인 메모인지 확인
        if (!memo.getUser().getId().equals(user.getId())) {
            throw new CustomException(BucketBoardErrorCode.NOT_OWN_MEMO);
        }

        if(memo.getState() == MemoState.COMPLETE) {
            throw new CustomException(BucketBoardErrorCode.MEMO_ALREADY_COMPLETED);
        }

        memo.complete();

        return bucketId;
    }

    @Transactional
    public BucketMoveResponse moveBucket(Long userId, Long bucketId, BucketMoveRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        BucketBoardMemo memo = bucketBoardMemoRepository.findById(bucketId)
                .orElseThrow(() -> new CustomException(
                        BucketBoardErrorCode.BUCKET_BOARD_NOT_FOUND
                ));

        // 본인 메모인지 확인
        if (!memo.getUser().getId().equals(user.getId())) {
            throw new CustomException(BucketBoardErrorCode.NOT_OWN_MEMO);
        }

        if(memo.getState() == MemoState.COMPLETE) {
            throw new CustomException(BucketBoardErrorCode.MEMO_ALREADY_COMPLETED);
        }

        memo.move(request.xPos(),  request.yPos());

        return BucketMoveResponse.of(bucketId, request.xPos(),  request.yPos());
    }

    public List<CategoryResponse> getCategory(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        List<MemoCategory> myCategories = memoCategoryRepository.findAllByUserId(userId);
        List<MemoCategory> defaultCategories = memoCategoryRepository.findAllByDefaultCategory(true);

        List<MemoCategory> categories = new ArrayList<>(defaultCategories);
        categories.addAll(myCategories);

        return categories.stream()
                .map(category -> CategoryResponse.of(
                        category.getMemoCategoryId(),
                        category.getCategoryName()
                ))
                .toList();

    }

    @Transactional
    public CategoryResponse createCategory(Long userId, CategoryRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        if(memoCategoryRepository.existsByCategoryNameAndUserId(request.categoryName(), userId)
        || memoCategoryRepository.existsByCategoryNameAndDefaultCategory(request.categoryName(), true)) {
            throw new CustomException(BucketBoardErrorCode.CATEGORY_NAME_DUPLICATED);
        }

        MemoCategory newCategory = MemoCategory.builder()
                .user(user)
                .categoryName(request.categoryName())
                .build();

        MemoCategory category = memoCategoryRepository.save(newCategory);

        return CategoryResponse.of(category.getMemoCategoryId(), category.getCategoryName());
    }

}
