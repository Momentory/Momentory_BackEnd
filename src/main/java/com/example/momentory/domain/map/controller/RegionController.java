package com.example.momentory.domain.map.controller;

import com.example.momentory.domain.map.dto.RegionResponseDto;
import com.example.momentory.domain.map.service.RegionService;
import com.example.momentory.global.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/region")
@Tag(name = "지역 API", description = "지역 관련 기능을 제공하는 API")
public class RegionController {

    private final RegionService regionService;

    @GetMapping
    @Operation(summary = "전체 지역 조회", description = "모든 지역 정보를 조회합니다.")
    public ApiResponse<List<RegionResponseDto.RegionDto>> getAllRegions() {
        List<RegionResponseDto.RegionDto> regions = regionService.getAllRegions();
        return ApiResponse.onSuccess(regions);
    }

    @GetMapping("/{regionId}")
    @Operation(summary = "지역 상세 조회 (ID)", description = "특정 지역의 상세 정보를 ID로 조회합니다.")
    public ApiResponse<RegionResponseDto.RegionDto> getRegionById(
            @Parameter(description = "지역 ID", required = true)
            @PathVariable Long regionId) {
        RegionResponseDto.RegionDto region = regionService.getRegionById(regionId);
        return ApiResponse.onSuccess(region);
    }

    @GetMapping("/name/{regionName}")
    @Operation(summary = "지역 상세 조회 (이름)", description = "특정 지역의 상세 정보를 이름으로 조회합니다.")
    public ApiResponse<RegionResponseDto.RegionDto> getRegionByName(
            @Parameter(description = "지역명 (예: 부천시)", required = true)
            @PathVariable String regionName) {
        RegionResponseDto.RegionDto region = regionService.getRegionByName(regionName);
        return ApiResponse.onSuccess(region);
    }
}
