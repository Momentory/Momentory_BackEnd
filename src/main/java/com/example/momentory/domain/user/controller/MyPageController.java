package com.example.momentory.domain.user.controller;

import com.example.momentory.domain.user.dto.UserRequestDto;
import com.example.momentory.domain.user.dto.UserResponseDto;
import com.example.momentory.domain.user.service.UserService;
import com.example.momentory.global.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/mypage")
@RequiredArgsConstructor
@Tag(name = "마이페이지 API", description = "마이페이지 관련 API")
public class MyPageController {
    private final UserService userService;

    @PutMapping("/profile")
    public ApiResponse<UserResponseDto.MyInfoDto> updateProfile(@RequestBody UserRequestDto.UpdateProfileDto request) {
        return ApiResponse.onSuccess(userService.updateProfile(request));
    }

    @GetMapping("/profile")
    public ApiResponse<UserResponseDto.MyInfoDto> getProfile() {
        return ApiResponse.onSuccess(userService.getMyInfo());
    }

    @GetMapping("/profile/{userId}")
    public ApiResponse<UserResponseDto.ProfileDto> getProfile(@PathVariable Long userId) {
        return ApiResponse.onSuccess(userService.getUserProfile(userId));
    }

    @DeleteMapping("")
    public ApiResponse<String> deleteProfile() {
        return ApiResponse.onSuccess(userService.deleteUser());
    }
}
