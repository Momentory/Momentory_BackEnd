package com.example.momentory.domain.roulette.service;

import com.example.momentory.domain.map.entity.Region;
import com.example.momentory.domain.map.repository.RegionRepository;
import com.example.momentory.domain.point.entity.PointActionType;
import com.example.momentory.domain.point.service.PointService;
import com.example.momentory.domain.roulette.dto.RouletteRequestDto;
import com.example.momentory.domain.roulette.dto.RouletteResponseDto;
import com.example.momentory.domain.roulette.entity.Roulette;
import com.example.momentory.domain.roulette.entity.RouletteType;
import com.example.momentory.domain.roulette.repository.RouletteRepository;
import com.example.momentory.domain.stamp.repository.StampRepository;
import com.example.momentory.domain.user.entity.User;
import com.example.momentory.domain.user.service.UserService;
import com.example.momentory.global.code.status.ErrorStatus;
import com.example.momentory.global.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class RouletteService {

    private final RouletteRepository rouletteRepository;
    private final RegionRepository regionRepository;
    private final StampRepository stampRepository;
    private final UserService userService;
    private final PointService pointService;

    private static final int ROULETTE_COST = 200;  // 룰렛 실행 비용
    private static final int ROULETTE_REWARD = 500;  // 룰렛 인증 보상
    private static final int MAX_REGIONS = 5;  // 룰렛에 표시할 최대 지역 수

    /**
     * 방문하지 않은 지역 5개 랜덤 조회
     */
    @Transactional(readOnly = true)
    public RouletteResponseDto.UnvisitedRegions getUnvisitedRegions() {
        User user = userService.getCurrentUser();

        // 모든 지역 조회
        List<Region> allRegions = regionRepository.findAll();

        // 사용자가 이미 방문한 지역 조회
        List<String> visitedRegions = stampRepository.findByUser(user).stream()
                .map(stamp -> stamp.getRegion())
                .distinct()
                .collect(Collectors.toList());

        // 방문하지 않은 지역 필터링
        List<String> unvisitedRegions = allRegions.stream()
                .map(Region::getName)
                .filter(regionName -> !visitedRegions.contains(regionName))
                .collect(Collectors.toList());

        // 방문하지 않은 지역이 없는 경우
        if (unvisitedRegions.isEmpty()) {
            throw new GeneralException(ErrorStatus.NO_UNVISITED_REGIONS);
        }

        // 랜덤으로 섞기
        Collections.shuffle(unvisitedRegions);

        // 최대 5개까지만 반환
        List<String> selectedRegions = unvisitedRegions.stream()
                .limit(MAX_REGIONS)
                .collect(Collectors.toList());

        return RouletteResponseDto.UnvisitedRegions.builder()
                .regions(selectedRegions)
                .build();
    }

    /**
     * 룰렛 스핀 (포인트 차감 및 미션 지역 저장)
     */
    @Transactional
    public RouletteResponseDto.SpinResult spinRoulette(RouletteRequestDto.SpinRoulette request) {
        User user = userService.getCurrentUser();

        // 포인트 부족 체크
        if (user.getProfile().getPoint() < ROULETTE_COST) {
            throw new GeneralException(ErrorStatus.INSUFFICIENT_POINTS);
        }

        // 선택된 지역이 실제 존재하는 지역인지 확인
        regionRepository.findByName(request.getSelectedRegion())
                .orElseThrow(() -> new GeneralException(ErrorStatus.REGION_NOT_FOUND));

        // 포인트 차감
        pointService.subtractPoint(ROULETTE_COST, PointActionType.ROULETTE);

        // 룰렛 생성 및 저장
        Roulette roulette = Roulette.builder()
                .user(user)
                .type(RouletteType.TRAVEL)
                .reward(request.getSelectedRegion())
                .usedPoint(ROULETTE_COST)
                .earnedPoint(0)  // 아직 인증 안 함
                .build();

        Roulette savedRoulette = rouletteRepository.save(roulette);

        // 인증 마감일 계산 (3일 후)
        LocalDateTime deadline = savedRoulette.getCreatedAt().plusDays(3);

        return RouletteResponseDto.SpinResult.builder()
                .rouletteId(savedRoulette.getRouletteId())
                .reward(savedRoulette.getReward())
                .usedPoint(ROULETTE_COST)
                .remainingPoint(user.getProfile().getPoint())
                .deadline(deadline)
                .build();
    }

    /**
     * 룰렛 인증 체크 및 보상 지급
     * @return 룰렛 인증 성공 여부
     */
    @Transactional
    public boolean checkAndCompleteRouletteReward(User user, String regionName) {
        LocalDateTime threeDaysAgo = LocalDateTime.now().minusDays(3);

        // 3일 이내 미완료 룰렛 중 해당 지역과 일치하는 것 찾기
        Optional<Roulette> activeRouletteOpt = rouletteRepository
                .findActiveRouletteByUserAndRegion(user, regionName, threeDaysAgo);

        if (activeRouletteOpt.isEmpty()) {
            return false;  // 룰렛 인증 대상 아님
        }

        Roulette roulette = activeRouletteOpt.get();

        // 룰렛 인증 완료 처리
        roulette.completeRouletteReward(ROULETTE_REWARD);

        // 보상 포인트 지급
        pointService.addPoint(user, ROULETTE_REWARD, PointActionType.ROULETTE_REWARD);

        log.info("[룰렛 인증 완료] user={}, region={}, reward={}",
                user.getUserId(), regionName, ROULETTE_REWARD);

        return true;
    }

    /**
     * 3일 이내 미완료 룰렛 중 해당 지역과 일치하는 것이 있는지 확인
     */
    @Transactional(readOnly = true)
    public boolean hasActiveRoulette(User user, String regionName) {
        LocalDateTime threeDaysAgo = LocalDateTime.now().minusDays(3);
        return rouletteRepository
                .findActiveRouletteByUserAndRegion(user, regionName, threeDaysAgo)
                .isPresent();
    }

    /**
     * 룰렛 내역 조회
     */
    @Transactional(readOnly = true)
    public RouletteResponseDto.RouletteHistory getRouletteHistory() {
        User user = userService.getCurrentUser();

        List<Roulette> roulettes = rouletteRepository.findByUserOrderByCreatedAtDesc(user);

        List<RouletteResponseDto.RouletteInfo> rouletteInfos = roulettes.stream()
                .map(roulette -> RouletteResponseDto.RouletteInfo.builder()
                        .rouletteId(roulette.getRouletteId())
                        .type(roulette.getType())
                        .reward(roulette.getReward())
                        .usedPoint(roulette.getUsedPoint())
                        .earnedPoint(roulette.getEarnedPoint())
                        .isCompleted(roulette.getEarnedPoint() > 0)
                        .createdAt(roulette.getCreatedAt())
                        .deadline(roulette.getCreatedAt().plusDays(3))
                        .build())
                .collect(Collectors.toList());

        return RouletteResponseDto.RouletteHistory.builder()
                .roulettes(rouletteInfos)
                .build();
    }

    /**
     * 미완료 룰렛 목록 조회
     */
    @Transactional(readOnly = true)
    public RouletteResponseDto.IncompleteRoulettes getIncompleteRoulettes() {
        User user = userService.getCurrentUser();

        List<Roulette> incompleteRoulettes = rouletteRepository.findIncompleteRoulettesByUser(user);

        List<RouletteResponseDto.RouletteInfo> rouletteInfos = incompleteRoulettes.stream()
                .map(roulette -> RouletteResponseDto.RouletteInfo.builder()
                        .rouletteId(roulette.getRouletteId())
                        .type(roulette.getType())
                        .reward(roulette.getReward())
                        .usedPoint(roulette.getUsedPoint())
                        .earnedPoint(roulette.getEarnedPoint())
                        .isCompleted(false)
                        .createdAt(roulette.getCreatedAt())
                        .deadline(roulette.getCreatedAt().plusDays(3))
                        .build())
                .collect(Collectors.toList());

        return RouletteResponseDto.IncompleteRoulettes.builder()
                .roulettes(rouletteInfos)
                .build();
    }
}

