package com.example.momentory.domain.character.controller;

import com.example.momentory.domain.character.dto.WardrobeDto;
import com.example.momentory.domain.character.service.WardrobeService;
import com.example.momentory.global.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wardrobe")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Wardrobe", description = "옷장 관련 API")
public class WardrobeController {

    private final WardrobeService wardrobeService;

    @PostMapping
    @Operation(summary = "옷장 슬롯 저장", description = "현재 캐릭터의 착용 상태를 옷장 슬롯으로 저장합니다.")
    public ApiResponse<WardrobeDto.Response> saveCurrentStyle() {
        WardrobeDto.Response response = wardrobeService.saveCurrentStyle(new WardrobeDto.CreateRequest());
        return ApiResponse.onSuccess(response);
    }

    @GetMapping
    @Operation(summary = "옷장 목록 조회", description = "저장된 옷장 슬롯 목록을 조회합니다.")
    public ApiResponse<List<WardrobeDto.ListResponse>> getMyWardrobes() {
        List<WardrobeDto.ListResponse> response = wardrobeService.getMyWardrobes();
        return ApiResponse.onSuccess(response);
    }

    @PatchMapping("/{wardrobeId}/apply")
    @Operation(summary = "옷장 스타일 적용", description = "저장된 옷장 스타일을 현재 캐릭터에 적용합니다.")
    public ApiResponse<WardrobeDto.Response> applyWardrobeStyle(@PathVariable Long wardrobeId) {
        WardrobeDto.Response response = wardrobeService.applyWardrobeStyle(wardrobeId);
        return ApiResponse.onSuccess(response);
    }
}
