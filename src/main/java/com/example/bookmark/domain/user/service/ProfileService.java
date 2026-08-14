package com.example.bookmark.domain.user.service;

import com.example.bookmark.domain.user.dto.request.ProfileUpdateRequest;
import com.example.bookmark.domain.user.dto.response.ProfileResponse;
import com.example.bookmark.domain.user.entity.User;
import com.example.bookmark.domain.user.exception.UserErrorCode;
import com.example.bookmark.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.bookmark.common.exception.CustomException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProfileService {

    private final UserRepository userRepository;

    @Transactional
    public ProfileResponse updateProfile(Long userId, ProfileUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        user.updateProfile(request.name(), request.bio(), request.profileImageUrl());
        // 변경 감지(dirty checking)로 자동 반영

        return ProfileResponse.from(user);
    }

    // AI 기능 ON/OFF 설정 변경
    @Transactional
    public boolean updateAiUse(Long userId, Boolean aiUse) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));

        user.updateAiUse(aiUse);

        return user.getAiUse();
    }
}