package com.example.momentory.domain.map.controller;

import com.example.momentory.domain.map.service.MapQueryService;
import com.example.momentory.domain.photo.dto.PhotoReseponseDto;
import com.example.momentory.global.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/map")
@Tag(name = "지도 API", description = "지도 관련 기능을 제공하는 API")
public class MapController {

    private final MapQueryService mapQueryService;

    // ========== 전체 지도 API ==========

    /**
     * 전체 지도 - 모든 지역의 최신 공개 사진들을 한번에 조회
     */
    @GetMapping("/public")
    @Operation(
        summary = "전체 지도 - 모든 지역 최신 공개 사진 조회",
        description = "모든 지역의 가장 최근에 업로드된 PUBLIC 사진들을 한번에 조회합니다. 클러스터링을 위한 API입니다.",
        tags = {"지도 API"}
    )
    public ApiResponse<Map<String, PhotoReseponseDto.PhotoResponse>> getAllRegionsLatestPublicPhotos() {
        Map<String, PhotoReseponseDto.PhotoResponse> photosMap = mapQueryService.getAllRegionsLatestPublicPhotos();
        return ApiResponse.onSuccess(photosMap);
    }

    /**
     * 전체 지도 - 특정 지역의 모든 공개 사진 조회 (클릭 시)
     */
    @GetMapping("/public/photos")
    @Operation(
        summary = "전체 지도 - 지역별 공개 사진 조회",
        description = "특정 지역의 PUBLIC 설정된 사진들을 모두 조회합니다. 사용자 상관없이 공개된 사진만 반환됩니다.",
        tags = {"지도 API"}
    )
    public ApiResponse<List<PhotoReseponseDto.PhotoResponse>> getPublicPhotosByRegion(
            @Parameter(description = "지역명 (예: 부천시)", required = true)
            @RequestParam String regionName) {
        List<PhotoReseponseDto.PhotoResponse> photos = mapQueryService.getPublicPhotosByRegion(regionName);
        return ApiResponse.onSuccess(photos);
    }

    // ========== 나의 지도 API ==========

    /**
     * 나의 지도 - 방문한 지역의 색깔 정보 조회
     */
    @GetMapping("/my")
    @Operation(
        summary = "나의 지도 - 방문한 지역의 색깔 정보 조회",
        description = "현재 로그인한 사용자가 방문한 지역만 색깔과 함께 반환합니다. 방문하지 않은 지역은 응답에 포함되지 않습니다.",
        tags = {"지도 API"}
    )
    public ApiResponse<Map<String, String>> getMyMapVisitStatus() {
        Map<String, String> visitStatusMap = mapQueryService.getAllRegionVisitStatus();
        return ApiResponse.onSuccess(visitStatusMap);
    }

    /**
     * 나의 지도 - 모든 지역의 내 최신 사진들을 한번에 조회
     */
    @GetMapping("/my/photos")
    @Operation(
        summary = "나의 지도 - 모든 지역 내 최신 사진 조회",
        description = "모든 지역의 현재 로그인한 사용자의 가장 최근 사진들을 한번에 조회합니다. 클러스터링을 위한 API입니다.",
        tags = {"지도 API"}
    )
    public ApiResponse<Map<String, PhotoReseponseDto.PhotoResponse>> getAllRegionsLatestMyPhotos() {
        Map<String, PhotoReseponseDto.PhotoResponse> photosMap = mapQueryService.getAllRegionsLatestUserPhotos();
        return ApiResponse.onSuccess(photosMap);
    }

    /**
     * 나의 지도 - 특정 지역의 내 모든 사진 조회 (확대/클릭 시)
     */
    @GetMapping("/my/photos/detail")
    @Operation(
        summary = "나의 지도 - 지역별 내 사진 조회",
        description = "특정 지역에 있는 현재 로그인한 사용자의 모든 사진을 조회합니다. visibility 설정과 상관없이 모든 사진이 반환됩니다.",
        tags = {"지도 API"}
    )
    public ApiResponse<List<PhotoReseponseDto.PhotoResponse>> getMyPhotosByRegion(
            @Parameter(description = "지역명 (예: 부천시)", required = true)
            @RequestParam String regionName) {
        List<PhotoReseponseDto.PhotoResponse> photos = mapQueryService.getUserPhotosByRegion(regionName);
        return ApiResponse.onSuccess(photos);
    }
}
