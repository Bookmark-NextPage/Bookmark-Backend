package com.example.bookmark.global.auth;

import io.swagger.v3.oas.annotations.Parameter;

import java.lang.annotation.*;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Parameter(hidden = true) // Swagger 입력칸 숨김 (토큰에서 자동 주입)
public @interface LoginUserId {
}