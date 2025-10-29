package com.example.momentory.domain.photo.controller;

import com.example.momentory.domain.photo.dto.StampRequestDto;
import com.example.momentory.domain.photo.service.StampService;
import com.example.momentory.global.ApiResponse;
import com.example.momentory.global.code.status.ErrorStatus;
import com.example.momentory.global.exception.GeneralException;
import com.example.momentory.global.security.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/stamps")
@Tag(name = "스탬프 API", description = "지역 스탬프 및 문화 스탬프 발급 기능을 제공합니다.")
public class StampController {

    private final StampService stampService;

    @PostMapping("/cultural")
    @Operation(summary = "문화 스탬프 발급", description = "문화시설명을 기반으로 문화 스탬프를 발급합니다.")
    public ApiResponse<String> grantCulturalStamp(@RequestBody StampRequestDto.CulturalStampSimpleRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) throw new GeneralException(ErrorStatus._UNAUTHORIZED);

        if (request.getSpotName() == null || request.getSpotName().isBlank()) {
            throw new GeneralException(ErrorStatus.INVALID_INPUT);
        }

        stampService.grantCulturalStampBySpotName(userId, request.getSpotName());
        return ApiResponse.onSuccess("문화 스탬프 발급 완료");
    }
}
