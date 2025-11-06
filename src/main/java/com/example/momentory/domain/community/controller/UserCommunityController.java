package com.example.momentory.domain.community.controller;

import com.example.momentory.domain.community.dto.PostResponseDto;
import com.example.momentory.domain.community.service.LikeService;
import com.example.momentory.domain.community.service.PostQueryService;
import com.example.momentory.domain.user.dto.UserResponseDto;
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
    private final LikeService likeService;

    @PostMapping("/follow/{userId}")
    @Operation(summary = "팔로우 토글", description = "특정 사용자를 팔로우 또는 해제합니다.")
    public ApiResponse<String> toggleFollow(@PathVariable Long userId) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        return ApiResponse.onSuccess(
                userService.toggleFollow(currentUserId, userId)
                        ? "팔로우했습니다." : "팔로우를 해제했습니다."
        );
    }

    @GetMapping("/users/me")
    @Operation(summary = "마이페이지 조회 (커뮤니티)", description = "현재 로그인 사용자의 프로필 정보 (팔로우 수, 팔로잉 수, 배경사진, 닉네임, bio, externalLink) 조회.")
    public ApiResponse<UserResponseDto.CommunityProfileDto> getMyProfile() {
        return ApiResponse.onSuccess(userService.getMyCommunityProfile());
    }

    @GetMapping("/users/{userId}")
    @Operation(summary = "다른 사용자 프로필 조회 (커뮤니티)", description = "특정 사용자의 프로필 정보 (팔로우 수, 팔로잉 수, 팔로우 여부, 배경사진, 닉네임, bio, externalLink) 조회.")
    public ApiResponse<UserResponseDto.CommunityProfileDto> getUserProfile(@PathVariable Long userId) {
        return ApiResponse.onSuccess(userService.getUserCommunityProfile(userId));
    }

    @GetMapping("/users/me/posts")
    @Operation(summary = "내가 쓴 글 조회", description = "현재 로그인 사용자가 작성한 게시글 목록 조회 (postId, imageUrl만 반환).")
    public ApiResponse<List<PostResponseDto.PostThumbnailDto>> getMyPosts() {
        return ApiResponse.onSuccess(postQueryService.getMyPosts());
    }

    @GetMapping("/users/{userId}/posts")
    @Operation(summary = "특정 사용자가 쓴 글 조회", description = "특정 사용자가 작성한 게시글 목록 조회 (postId, imageUrl만 반환).")
    public ApiResponse<List<PostResponseDto.PostThumbnailDto>> getUserPosts(@PathVariable Long userId) {
        return ApiResponse.onSuccess(postQueryService.getUserPosts(userId));
    }

    @GetMapping("/users/me/comments")
    @Operation(summary = "내가 댓글 단 글 조회", description = "현재 로그인 사용자가 댓글 단 게시글 목록 조회 (postId, imageUrl만 반환).")
    public ApiResponse<List<PostResponseDto.PostThumbnailDto>> getPostsICommented() {
        return ApiResponse.onSuccess(postQueryService.getPostsICommented());
    }

    @GetMapping("/users/me/likes")
    @Operation(summary = "내가 좋아요 누른 글 조회", description = "현재 로그인 사용자가 좋아요한 게시글 목록 조회 (postId, imageUrl만 반환).")
    public ApiResponse<List<PostResponseDto.PostThumbnailDto>> getUserLikedPosts() {
        return ApiResponse.onSuccess(likeService.getUserLikedPosts());
    }
}
