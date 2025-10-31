package com.example.momentory.domain.stamp.controller;

import com.example.momentory.domain.stamp.dto.StampRequestDto;
import com.example.momentory.domain.stamp.dto.StampResponseDto;
import com.example.momentory.domain.stamp.entity.StampType;
import com.example.momentory.domain.stamp.service.StampService;
import com.example.momentory.global.ApiResponse;
import com.example.momentory.global.code.status.ErrorStatus;
import com.example.momentory.global.exception.GeneralException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/stamps")
@Tag(name = "스탬프 API", description = "지역 스탬프 및 문화 스탬프 발급 기능을 제공합니다.")
public class StampController {

    private final StampService stampService;

    @PostMapping("/cultural")
    @Operation(summary = "문화 스탬프 발급", description = "문화시설명을 기반으로 문화 스탬프를 발급합니다.")
    public ApiResponse<String> grantCulturalStamp(@RequestBody StampRequestDto.CulturalStampSimpleRequest request) {
        if (request.getSpotName() == null || request.getSpotName().isBlank()) {
            throw new GeneralException(ErrorStatus.INVALID_INPUT);
        }

        stampService.grantCulturalStamp(request.getSpotName());
        return ApiResponse.onSuccess("문화 스탬프 발급 완료");
    }

    @GetMapping("/my")
    @Operation(summary = "나의 스탬프 조회", description = "타입별로 사용자의 스탬프를 조회합니다. type 파라미터가 없으면 REGIONAL/CULTURAL을 모두 그룹으로 반환합니다.")
    public ApiResponse<?> getMyStamps(@RequestParam(value = "type", required = false) StampType type) {
        if (type == null) {
            return ApiResponse.onSuccess(stampService.getMyStampsGrouped());
        }
        return ApiResponse.onSuccess(stampService.getMyStampsByType(type));
    }

    @GetMapping("/recent")
    @Operation(summary = "최근에 얻은 스탬프 조회", description = "타입 구분 없이 사용자가 최근에 획득한 스탬프 3개를 조회합니다.")
    public ApiResponse<List<StampResponseDto.StampInfo>> getRecentStamps(){
        return ApiResponse.onSuccess(stampService.getRecentStampsByUser());
    }
}
