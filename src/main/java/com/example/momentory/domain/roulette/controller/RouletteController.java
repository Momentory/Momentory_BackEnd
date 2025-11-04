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

    @GetMapping("/unvisited-regions")
    @Operation(summary = "방문하지 않은 지역 5개 랜덤 조회", 
            description = "룰렛을 돌리기 위해 방문하지 않은 지역 최대 5개를 랜덤으로 조회합니다.")
    public ApiResponse<RouletteResponseDto.UnvisitedRegions> getUnvisitedRegions() {
        RouletteResponseDto.UnvisitedRegions response = rouletteService.getUnvisitedRegions();
        return ApiResponse.of(SuccessStatus._OK, response);
    }

    @PostMapping("/spin")
    @Operation(summary = "룰렛 스핀", 
            description = "룰렛을 돌려 미션 지역을 결정하고 포인트를 차감합니다. (200p 차감)")
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

