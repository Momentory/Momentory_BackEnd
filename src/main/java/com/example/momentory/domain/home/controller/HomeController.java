package com.example.momentory.domain.home.controller;

import com.example.momentory.domain.character.dto.CharacterDto;
import com.example.momentory.domain.home.dto.HomeDto;
import com.example.momentory.domain.home.service.HomeService;
import com.example.momentory.domain.user.entity.User;
import com.example.momentory.domain.user.repository.UserRepository;
import com.example.momentory.global.ApiResponse;
import com.example.momentory.global.security.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/home")
@Tag(name = "홈 API", description = "홈 화면용 요약 데이터 제공")
public class HomeController {

    private final HomeService homeService;
    private final UserRepository userRepository;

    @GetMapping("/travel-top3")
    @Operation(summary = "오늘의 여행지 Top 3", description = "추천 여행지 상위 3개를 반환합니다.")
    public ApiResponse<List<HomeDto.TravelSpotSummary>> getTop3TravelSpots() {
        return ApiResponse.onSuccess(homeService.getTop3TravelSpots());
    }

    @GetMapping("/recent-photos")
    @Operation(summary = "최신 업로드 사진 3개", description = "최근 업로드된 공개 사진 3개를 반환합니다.")
    public ApiResponse<List<HomeDto.RecentPhotoSummary>> getRecentPhotos() {
        return ApiResponse.onSuccess(homeService.getRecentPhotos());
    }

    @GetMapping("/character-status")
    @Operation(summary = "나의 캐릭터 현황", description = "현재 선택된 캐릭터와 착용 아이템 현황을 반환합니다.")
    public ApiResponse<CharacterDto.CurrentCharacterResponse> getMyCharacterStatus() {
        Long userId = SecurityUtils.getCurrentUserId();
        User user = userRepository.findById(userId).orElseThrow();
        return ApiResponse.onSuccess(homeService.getMyCharacterStatus(user));
    }

    @GetMapping("/events")
    @Operation(summary = "다가오는 이벤트", description = "다가오는 축제/이벤트 목록을 반환합니다.")
    public ApiResponse<List<HomeDto.EventSummary>> getUpcomingEvents() {
        return ApiResponse.onSuccess(homeService.getUpcomingEvents());
    }
}


