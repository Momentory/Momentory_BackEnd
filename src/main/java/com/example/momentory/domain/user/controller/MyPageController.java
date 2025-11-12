package com.example.momentory.domain.user.controller;

import com.example.momentory.domain.photo.dto.PhotoRequestDto;
import com.example.momentory.domain.photo.dto.PhotoReseponseDto;
import com.example.momentory.domain.photo.service.PhotoService;
import com.example.momentory.domain.user.dto.UserRequestDto;
import com.example.momentory.domain.user.dto.UserResponseDto;
import com.example.momentory.domain.user.service.UserService;
import com.example.momentory.global.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/mypage")
@RequiredArgsConstructor
@Tag(name = "마이페이지 API", description = "마이페이지 관련 API")
public class MyPageController {
    private final UserService userService;
    private final PhotoService photoService;

    @PutMapping("/profile")
    @Operation(summary = "내 정보 수정", description = "내 정보를 수정하는 API입니다.")
    public ApiResponse<UserResponseDto.MyInfoDto> updateProfile(@RequestBody UserRequestDto.UpdateProfileDto request) {
        return ApiResponse.onSuccess(userService.updateProfile(request));
    }

    @GetMapping("/profile")
    @Operation(summary = "내 정보 조회", description = "내 정보를 조회하는 API입니다.")
    public ApiResponse<UserResponseDto.MyInfoDto> getProfile() {
        return ApiResponse.onSuccess(userService.getMyInfo());
    }

    @GetMapping("/profile/summary")
    @Operation(summary = "내 정보 요약 조회", description = "내 정보를 요약하여 조회하는 API입니다.")
    public ApiResponse<UserResponseDto.MySummaryInfoDto> getSummaryProfile() {
        return ApiResponse.onSuccess(userService.getMySummaryInfo());
    }

    @DeleteMapping("")
    @Operation(summary = "회원 탈퇴", description = "회원을 탈퇴하는 API입니다.")
    public ApiResponse<String> deleteProfile() {
        return ApiResponse.onSuccess(userService.deleteUser());
    }

    @GetMapping("/photos")
    @Operation(summary = "내 사진 목록 조회", description = "내가 올린 사진 목록을 커서 페이지네이션으로 조회하는 API입니다.")
    public ApiResponse<PhotoReseponseDto.MyPhotosCursorResponse> getMyPhotos(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime cursor,
            @RequestParam(required = false, defaultValue = "20") Integer size) {
        PhotoRequestDto.MyPhotosCursorRequest request = new PhotoRequestDto.MyPhotosCursorRequest();
        request.setCursor(cursor);
        request.setSize(size);
        return ApiResponse.onSuccess(photoService.getMyPhotos(request));
    }
}
