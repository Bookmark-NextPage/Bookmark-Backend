package com.example.bookmark.domain.record.service;

import com.example.bookmark.common.exception.CustomException;
import com.example.bookmark.domain.record.dto.request.TagCreateRequest;
import com.example.bookmark.domain.record.dto.response.TagResponse;
import com.example.bookmark.domain.record.entity.Keyword;
import com.example.bookmark.domain.record.exception.RecordErrorCode;
import com.example.bookmark.domain.record.repository.TagRepository;
import com.example.bookmark.domain.user.entity.User;
import com.example.bookmark.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TagService {

    private final TagRepository tagRepository;
    private final UserRepository userRepository;

    private static final int MAX_TAG_LIMIT = 10;

    @Transactional
    public TagResponse createTag(Long userId, TagCreateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(RecordErrorCode.USER_NOT_FOUND));

        if (tagRepository.countByUserId(userId) >= MAX_TAG_LIMIT) {
            throw new CustomException(RecordErrorCode.TAG_LIMIT_EXCEEDED, "태그는 최대 10개까지만 생성할 수 있습니다.");
        }

        if (tagRepository.existsByUserIdAndName(userId, request.getName())) {
            throw new CustomException(RecordErrorCode.DUPLICATE_TAG_NAME, "이미 존재하는 태그 이름입니다.");
        }

        Keyword keyword = Keyword.builder()
                .user(user)
                .name(request.getName())
                .build();

        Keyword savedKeyword = tagRepository.save(keyword);
        return TagResponse.from(savedKeyword);
    }

    public List<TagResponse> getMyTags(Long userId) {
        List<Keyword> keywords = tagRepository.findAllByUserIdOrderByIdDesc(userId);
        return keywords.stream()
                .map(TagResponse::from)
                .toList();
    }
}