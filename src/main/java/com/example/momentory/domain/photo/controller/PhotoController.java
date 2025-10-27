package com.example.momentory.domain.photo.controller;

import com.example.momentory.domain.photo.dto.PhotoRequestDto;
import com.example.momentory.domain.photo.dto.PhotoReseponseDto;
import com.example.momentory.domain.photo.service.PhotoService;
import com.example.momentory.global.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/photos")
public class PhotoController {

    private final PhotoService photoService;

    // 포토 업로드
    @PostMapping
    public ApiResponse<PhotoReseponseDto.PhotoUploadResponse> uploadPhoto(@RequestBody PhotoRequestDto.PhotoUpload photoRequest) {
        return ApiResponse.onSuccess(photoService.uploadPhoto(photoRequest));
    }

    // 포토 수정
    @PatchMapping("/{photoId}")
    public ApiResponse<PhotoReseponseDto.PhotoResponse> updatePhoto(
            @PathVariable Long photoId,
            @RequestBody PhotoRequestDto.PhotoUpdate photoRequest) {
        return ApiResponse.onSuccess(photoService.updatePhoto(photoId, photoRequest));
    }

    // 포토 삭제
    @DeleteMapping("/{photoId}")
    public ApiResponse<String> deletePhoto(@PathVariable Long photoId) {
        photoService.deletePhoto(photoId);
        return ApiResponse.onSuccess("포토가 성공적으로 삭제되었습니다.");
    }

    // 포토 조회
    @GetMapping("/{photoId}")
    public ApiResponse<PhotoReseponseDto.PhotoResponse> getPhoto(@PathVariable Long photoId) {
        return ApiResponse.onSuccess(photoService.getPhoto(photoId));
    }

    // 포토 공개 여부 변경
    @PutMapping("/{photoId}/visibility")
    public ApiResponse<PhotoReseponseDto.PhotoResponse> changePhotoVisibility(
            @PathVariable Long photoId,
            @RequestBody PhotoRequestDto.VisibilityChange visibilityRequest) {
        return ApiResponse.onSuccess(photoService.changePhotoVisibility(photoId, visibilityRequest));
    }
}
