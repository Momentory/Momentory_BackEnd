package com.example.momentory.domain.point.controller;

import com.example.momentory.domain.character.dto.CharacterDto;
import com.example.momentory.domain.point.dto.PointRequest;
import com.example.momentory.domain.point.dto.PointResponse;
import com.example.momentory.domain.point.entity.PointActionType;
import com.example.momentory.domain.point.service.PointService;
import com.example.momentory.domain.user.entity.User;
import com.example.momentory.domain.user.repository.UserRepository;
import com.example.momentory.global.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/point")
@RequiredArgsConstructor
@Tag(name = "포인트 API", description = "포인트 API")
public class PointController {
    private final PointService pointService;
    private final UserRepository userRepository;

    @GetMapping("")
    @Operation(summary = "내 포인트 조회", description = "사용자의 캐릭터 레벨, 누적포인트, 현재 포인트를 조회합니다.")
    public ApiResponse<PointResponse.CharacterPoint> getPointUser() {
        return ApiResponse.onSuccess(pointService.getUserPoint());
    }

    @PostMapping("/add")
    @Operation(summary = "포인트 추가", description = "사용자의 포인트를 추가합니다.")
    public ApiResponse<String> addPoint(@RequestBody PointRequest.Add request) {
        if(request.getUserId()!=null){
            User user = userRepository.findById(request.getUserId()).get();
            pointService.addPoint(user, request.getAmount(), request.getAction());
        }
        else pointService.addPoint(request.getAmount(), request.getAction());
        return ApiResponse.onSuccess("포인트가 추가되었습니다.");
    }

    @PostMapping("/use")
    @Operation(summary = "포인트 사용", description = "사용자의 포인트를 감소합니다.")
    public ApiResponse<String> usePoint(@RequestBody PointRequest.Use request) {
        if(request.getUserId()!=null){
            User user = userRepository.findById(request.getUserId()).get();
            pointService.subtractPoint(user, request.getAmount(), request.getAction());
        }
        else pointService.subtractPoint(request.getAmount(), request.getAction());
        return ApiResponse.onSuccess("포인트가 차감되었습니다.");
    }

    @GetMapping("/history")
    @Operation(summary = "포인트 내역", description = "사용자의 포인트 내역을 조회합니다.")
    public ApiResponse<PointResponse.PointHistory> pointHistory(){
        return ApiResponse.onSuccess(pointService.getPointHistory());
    }

}
