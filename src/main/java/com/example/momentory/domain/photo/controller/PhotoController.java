package com.example.momentory.domain.photo.controller;

import com.example.momentory.domain.photo.dto.PhotoRequestDto;
import com.example.momentory.domain.photo.dto.PhotoReseponseDto;
import com.example.momentory.domain.photo.service.PhotoService;
import com.example.momentory.global.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/photos")
@Tag(name = "사진 API", description = "사진 업로드, 수정, 삭제, 조회 및 관련 기능을 제공합니다.")
public class PhotoController {

    private final PhotoService photoService;

    @PostMapping
    @Operation(summary = "사진 업로드", description = "새로운 사진을 업로드하고 지역 스탬프 및 문화시설 스탬프를 발급합니다.")
    public ApiResponse<PhotoReseponseDto.PhotoUploadResponse> uploadPhoto(@RequestBody PhotoRequestDto.PhotoUpload photoRequest) {
        return ApiResponse.onSuccess(photoService.uploadPhoto(photoRequest));
    }

    @PatchMapping("/{photoId}")
    @Operation(summary = "사진 수정", description = "기존 사진의 주소, 메모, 공개 여부를 수정합니다.")
    public ApiResponse<PhotoReseponseDto.PhotoResponse> updatePhoto(
            @PathVariable Long photoId,
            @RequestBody PhotoRequestDto.PhotoUpdate photoRequest) {
        return ApiResponse.onSuccess(photoService.updatePhoto(photoId, photoRequest));
    }

    @DeleteMapping("/{photoId}")
    @Operation(summary = "사진 삭제", description = "지정된 사진을 완전히 삭제합니다.")
    public ApiResponse<String> deletePhoto(@PathVariable Long photoId) {
        photoService.deletePhoto(photoId);
        return ApiResponse.onSuccess("포토가 성공적으로 삭제되었습니다.");
    }

    @GetMapping("/{photoId}")
    @Operation(summary = "사진 조회", description = "지정된 사진의 상세 정보를 조회합니다.")
    public ApiResponse<PhotoReseponseDto.PhotoResponse> getPhoto(@PathVariable Long photoId) {
        return ApiResponse.onSuccess(photoService.getPhoto(photoId));
    }

    @PutMapping("/{photoId}/visibility")
    @Operation(summary = "사진 공개 여부 변경", description = "사진의 공개/비공개 상태를 변경합니다.")
    public ApiResponse<PhotoReseponseDto.PhotoResponse> changePhotoVisibility(
            @PathVariable Long photoId,
            @RequestBody PhotoRequestDto.VisibilityChange visibilityRequest) {
        return ApiResponse.onSuccess(photoService.changePhotoVisibility(photoId, visibilityRequest));
    }

    @PutMapping("/{photoId}/nearby")
    @Operation(summary = "근처 관광지 추천", description = "사진의 위치를 기반으로 근처 관광지를 추천합니다.")
    public ApiResponse<PhotoReseponseDto.NearbySpotsResponse> getNearbySpots(
            @PathVariable Long photoId) {
        return ApiResponse.onSuccess(photoService.getNearbySpots(photoId));
    }

    @PostMapping("/location-to-address")
    @Operation(summary = "위치 정보 주소 변환", description = "위도와 경도 좌표를 주소로 변환합니다.")
    public ApiResponse<PhotoReseponseDto.LocationToAddressResponse> convertLocationToAddress(
            @RequestBody PhotoRequestDto.LocationToAddressRequest request) {
        return ApiResponse.onSuccess(photoService.convertLocationToAddress(request));
    }
}
