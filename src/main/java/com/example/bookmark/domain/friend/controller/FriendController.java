package com.example.bookmark.domain.friend.controller;

import com.example.bookmark.domain.friend.dto.request.FriendAddRequest;
import com.example.bookmark.domain.friend.dto.response.FriendPageResponse;
import com.example.bookmark.domain.friend.dto.response.FriendRequestResponse;
import com.example.bookmark.domain.friend.dto.response.UserSummaryResponse;
import com.example.bookmark.domain.friend.service.FriendService;
import com.example.bookmark.global.auth.LoginUserId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Friend", description = "친구 API")
@RestController
@RequestMapping("/api/v1/friends")
@RequiredArgsConstructor
public class FriendController {

    private final FriendService friendService;

    @Operation(summary = "유저 검색", description = "이름으로 유저를 검색합니다. (본인 제외)")
    @GetMapping("/search")
    public ResponseEntity<List<UserSummaryResponse>> search(
            @LoginUserId Long meId,
            @RequestParam String name
    ) {
        return ResponseEntity.ok(friendService.searchByName(meId, name));
    }

    @Operation(summary = "친구 신청", description = "검색한 유저에게 친구 신청을 보냅니다.")
    @PostMapping
    public ResponseEntity<UserSummaryResponse> addFriend(
            @LoginUserId Long meId,
            @Valid @RequestBody FriendAddRequest request
    ) {
        UserSummaryResponse response = friendService.addFriend(meId, request.friendUserId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "친구 목록", description = "수락되어 친구가 된 유저 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<List<UserSummaryResponse>> getFriends(@LoginUserId Long meId) {
        return ResponseEntity.ok(friendService.getFriends(meId));
    }

    @Operation(summary = "친구 삭제", description = "friendUserId에 해당하는 친구를 삭제합니다.")
    @DeleteMapping("/{friendUserId}")
    public ResponseEntity<Void> deleteFriend(
            @LoginUserId Long meId,
            @PathVariable Long friendUserId
    ) {
        friendService.deleteFriend(meId, friendUserId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "받은 친구 신청 목록(알림)", description = "나에게 온 대기중인 친구 신청을 조회합니다.")
    @GetMapping("/requests/received")
    public ResponseEntity<List<FriendRequestResponse>> getReceivedRequests(@LoginUserId Long meId) {
        return ResponseEntity.ok(friendService.getReceivedRequests(meId));
    }

    @Operation(summary = "친구 신청 수락", description = "받은 친구 신청을 수락합니다.")
    @PostMapping("/requests/{requestId}/accept")
    public ResponseEntity<Void> acceptRequest(
            @LoginUserId Long meId,
            @PathVariable Long requestId
    ) {
        friendService.acceptRequest(meId, requestId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "친구 신청 거절", description = "받은 친구 신청을 거절합니다.")
    @PostMapping("/requests/{requestId}/reject")
    public ResponseEntity<Void> rejectRequest(
            @LoginUserId Long meId,
            @PathVariable Long requestId
    ) {
        friendService.rejectRequest(meId, requestId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "친구 페이지 조회", description = "친구의 프로필과 공개해둔 콜렉트북을 조회합니다. (친구만 가능)")
    @GetMapping("/{friendUserId}/page")
    public ResponseEntity<FriendPageResponse> getFriendPage(
            @LoginUserId Long meId,
            @PathVariable Long friendUserId
    ) {
        return ResponseEntity.ok(friendService.getFriendPage(meId, friendUserId));
    }
}