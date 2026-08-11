package com.example.bookmark.domain.home.controller;

import com.example.bookmark.domain.home.dto.response.HomeResponse;
import com.example.bookmark.domain.home.service.HomeService;
import com.example.bookmark.global.auth.LoginUserId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Home", description = "홈 API")
@RestController
@RequestMapping("/api/home")
@RequiredArgsConstructor
public class HomeController {

    private final HomeService homeService;

    @Operation(
            summary = "홈",
            description = ""
    )
    @GetMapping("")
    public ResponseEntity<HomeResponse> home(
            @LoginUserId Long userId
    ) {
        return ResponseEntity.ok(homeService.getHome(userId));
    }
}
