package com.example.momentory.domain.roulette.controller;

import com.example.momentory.domain.roulette.dto.RouletteRequestDto;
import com.example.momentory.domain.roulette.dto.RouletteResponseDto;
import com.example.momentory.domain.roulette.service.RouletteService;
import com.example.momentory.global.ApiResponse;
import com.example.momentory.global.code.status.SuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/roulette")
@RequiredArgsConstructor
@Tag(name = "룰렛", description = "룰렛 관련 API")
public class RouletteController {

    private final RouletteService rouletteService;

    @GetMapping("/slots")
    @Operation(summary = "룰렛 슬롯 8개 랜덤 조회 (지역 + 아이템)",
            description = "룰렛을 돌리기 위해 방문하지 않은 지역과 아이템을 포함한 최대 8개의 슬롯을 랜덤으로 조회합니다. 방문하지 않은 지역이 8개 이상이면 대부분 지역, 8개 이하이면 나머지는 아이템으로 채워집니다.")
    public ApiResponse<RouletteResponseDto.RouletteSlots> getRouletteSlots() {
        RouletteResponseDto.RouletteSlots response = rouletteService.getRouletteSlots();
        return ApiResponse.of(SuccessStatus._OK, response);
    }

    @PostMapping("/spin")
    @Operation(summary = "룰렛 스핀",
            description = "룰렛을 돌려 미션 지역을 결정하거나 아이템을 획득하고 포인트를 차감합니다. (200p 차감) 지역이 선택되면 3일 이내 인증 미션이 생성되고, 아이템이 선택되면 바로 아이템이 지급됩니다.")
    public ApiResponse<RouletteResponseDto.SpinResult> spinRoulette(
            @RequestBody RouletteRequestDto.SpinRoulette request) {
        RouletteResponseDto.SpinResult response = rouletteService.spinRoulette(request);
        return ApiResponse.of(SuccessStatus._OK, response);
    }

    @GetMapping("/history")
    @Operation(summary = "룰렛 내역 조회", 
            description = "사용자의 모든 룰렛 내역을 조회합니다.")
    public ApiResponse<RouletteResponseDto.RouletteHistory> getRouletteHistory() {
        RouletteResponseDto.RouletteHistory response = rouletteService.getRouletteHistory();
        return ApiResponse.of(SuccessStatus._OK, response);
    }

    @GetMapping("/incomplete")
    @Operation(summary = "미완료 룰렛 목록 조회", 
            description = "아직 인증하지 않은 룰렛 목록을 조회합니다.")
    public ApiResponse<RouletteResponseDto.IncompleteRoulettes> getIncompleteRoulettes() {
        RouletteResponseDto.IncompleteRoulettes response = rouletteService.getIncompleteRoulettes();
        return ApiResponse.of(SuccessStatus._OK, response);
    }
}

