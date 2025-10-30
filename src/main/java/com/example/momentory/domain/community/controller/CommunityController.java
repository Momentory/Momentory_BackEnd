package com.example.momentory.domain.community.controller;

import com.example.momentory.domain.community.service.CommunityService;
import com.example.momentory.global.ApiResponse; // ApiResponse 클래스 import (친구분 코드 형식 유지)
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity; // ResponseEntity는 좋아요 토글 응답을 위해 사용 가능

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
@Tag(name = "커뮤니티 API", description = "게시글 및 좋아요 관련 API")
public class CommunityController {

    private final CommunityService communityService;

    // TODO: 여기에 게시글 조회/생성/수정/삭제 등의 API가 추가되어야 합니다.

    /**
     * POST /api/v1/posts/{postId}/like : 좋아요 토글 (설정 또는 취소)
     */
    @PostMapping("/{postId}/like")
    @Operation(summary = "게시글 좋아요 토글", description = "특정 게시글에 좋아요를 설정하거나 취소합니다.")
    public ApiResponse<String> toggleLike(@PathVariable Long postId) {

        // 🚨 임시 userId 설정:
        // 실제로는 인증 로직(JWT 토큰 등)을 통해 현재 로그인한 사용자 ID를 가져와야 합니다.
        Long userId = 1L;

        // CommunityService의 토글 로직 실행
        boolean isLiked = communityService.toggleLike(userId, postId);

        if (isLiked) {
            // 좋아요 설정 성공 응답 (ApiResponse 형식 사용)
            return ApiResponse.onSuccess("게시글에 좋아요를 설정했습니다.");
        } else {
            // 좋아요 취소 성공 응답
            return ApiResponse.onSuccess("게시글의 좋아요를 취소했습니다.");
        }
    }
}