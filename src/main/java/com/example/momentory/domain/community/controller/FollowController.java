package com.example.momentory.domain.community.controller;

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
@Tag(name = "커뮤니티 - 팔로우", description = "팔로우 목록 조회 API")
public class FollowController {

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

    @GetMapping("/followers/me")
    @Operation(summary = "팔로우 목록 조회", description = "현재 로그인 사용자를 팔로우하는 사람들의 목록을 조회합니다. (userId, nickname, 프로필사진만 반환)")
    public ApiResponse<List<UserResponseDto.FollowUserDto>> getMyFollowers() {
        return ApiResponse.onSuccess(userService.getMyFollowers());
    }

    @GetMapping("/followings/me")
    @Operation(summary = "팔로잉 목록 조회", description = "현재 로그인 사용자가 팔로우하는 사람들의 목록을 조회합니다. (userId, nickname, 프로필사진만 반환)")
    public ApiResponse<List<UserResponseDto.FollowUserDto>> getMyFollowings() {
        return ApiResponse.onSuccess(userService.getMyFollowings());
    }
}