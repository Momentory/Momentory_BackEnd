package com.example.momentory.domain.photo.controller;

import com.example.momentory.domain.photo.dto.StampRequestDto;
import com.example.momentory.domain.photo.service.StampService;
import com.example.momentory.global.ApiResponse;
import com.example.momentory.global.code.status.ErrorStatus;
import com.example.momentory.global.exception.GeneralException;
import com.example.momentory.global.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/stamps")
public class StampController {

    private final StampService stampService;

    /**
     * 문화 스탬프 발급 (spotName만 전달받음)
     */
    @PostMapping("/cultural")
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
