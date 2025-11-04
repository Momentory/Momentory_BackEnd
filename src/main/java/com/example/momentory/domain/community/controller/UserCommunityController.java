package com.example.momentory.domain.community.controller;

import com.example.momentory.domain.community.dto.PostResponseDto;
import com.example.momentory.domain.community.service.PostQueryService;
import com.example.momentory.domain.user.service.UserService;
import com.example.momentory.global.ApiResponse;
import com.example.momentory.global.security.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/community")
@RequiredArgsConstructor
@Tag(name = "커뮤니티 - 사용자/팔로우", description = "팔로우 및 사용자별 조회 API")
public class UserCommunityController {

    private final PostQueryService postQueryService;
    private final UserService userService;

    @PostMapping("/follow/{userId}")
    @Operation(summary = "팔로우 토글", description = "특정 사용자를 팔로우 또는 해제합니다.")
    public ApiResponse<String> toggleFollow(@PathVariable Long userId) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        return ApiResponse.onSuccess(
                userService.toggleFollow(currentUserId, userId)
                        ? "팔로우했습니다." : "팔로우를 해제했습니다."
        );
    }

    @GetMapping("/users/me/posts")
    @Operation(summary = "내가 쓴 글 조회", description = "현재 로그인 사용자가 작성한 게시글 목록 조회.")
    public ApiResponse<List<PostResponseDto.PostDto>> getMyPosts() {
        return ApiResponse.onSuccess(postQueryService.getMyPosts());
    }

    @GetMapping("/users/me/comments")
    @Operation(summary = "내가 댓글 단 글 조회", description = "현재 로그인 사용자가 댓글 단 게시글 목록 조회.")
    public ApiResponse<List<PostResponseDto.PostDto>> getPostsICommented() {
        return ApiResponse.onSuccess(postQueryService.getPostsICommented());
    }
}
