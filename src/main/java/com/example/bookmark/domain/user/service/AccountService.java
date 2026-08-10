package com.example.bookmark.domain.user.service;

import com.example.bookmark.domain.collectBook.repository.CollectBookRepository;
import com.example.bookmark.domain.friend.repository.FriendRepository;
import com.example.bookmark.domain.user.entity.User;
import com.example.bookmark.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AccountService {

    private final UserRepository userRepository;
    private final FriendRepository friendRepository;
    private final CollectBookRepository collectBookRepository;
    private final PasswordEncoder passwordEncoder;

    // 로그아웃
    public void logout(Long userId) {
        // TODO: 강제 만료가 필요하면 토큰 블랙리스트(Redis 등) 도입.
    }

    // 회원 탈퇴
    @Transactional
    public void withdraw(Long userId, String password) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        // 본인 확인: 비밀번호 재확인
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        // 연관 데이터 정리
        friendRepository.deleteAllByUserIdOrFriendUserId(userId, userId); // 친구 관계
        collectBookRepository.deleteAllByUserId(userId);               // 콜렉트북(+챕터)
        // TODO: 버킷보드/기록 도메인 구현 후 해당 데이터 정리 추가

        userRepository.deleteById(userId);
    }
}