package com.example.bookmark.domain.user.service;

import com.example.bookmark.domain.user.dto.request.ProfileUpdateRequest;
import com.example.bookmark.domain.user.dto.response.ProfileResponse;
import com.example.bookmark.domain.user.entity.User;
import com.example.bookmark.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}