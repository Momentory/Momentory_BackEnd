package com.example.momentory.domain.roulette.service;

import com.example.momentory.domain.character.entity.CharacterItem;
import com.example.momentory.domain.character.entity.UserItem;
import com.example.momentory.domain.character.repository.CharacterItemRepository;
import com.example.momentory.domain.character.repository.UserItemRepository;
import com.example.momentory.domain.map.entity.Region;
import com.example.momentory.domain.map.repository.RegionRepository;
import com.example.momentory.domain.point.entity.PointActionType;
import com.example.momentory.domain.point.service.PointService;
import com.example.momentory.domain.roulette.dto.RouletteRequestDto;
import com.example.momentory.domain.roulette.dto.RouletteResponseDto;
import com.example.momentory.domain.roulette.entity.Roulette;
import com.example.momentory.domain.roulette.entity.RouletteSlotType;
import com.example.momentory.domain.roulette.entity.RouletteStatus;
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
import java.util.ArrayList;
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
    private final CharacterItemRepository characterItemRepository;
    private final UserItemRepository userItemRepository;
    private final UserService userService;
    private final PointService pointService;

    private static final int MAX_SLOTS = 8;  // 룰렛에 표시할 최대 슬롯 수 (지역 + 아이템)

    /**
     * 룰렛 슬롯 8개 랜덤 조회 (지역 + 아이템)
     * - 방문하지 않은 지역이 8개 이상: 대부분 지역, 일부 아이템
     * - 방문하지 않은 지역이 8개 이하: 모든 미방문 지역 + 아이템으로 채우기
     */
    @Transactional(readOnly = true)
    public RouletteResponseDto.RouletteSlots getRouletteSlots() {
        User user = userService.getCurrentUser();

        // 모든 지역 조회
        List<Region> allRegions = regionRepository.findAll();

        // 사용자가 이미 방문한 지역 조회
        List<String> visitedRegions = stampRepository.findByUser(user).stream()
                .map(stamp -> stamp.getRegion())
                .distinct()
                .collect(Collectors.toList());

        // 방문하지 않은 지역 필터링
        List<Region> unvisitedRegions = allRegions.stream()
                .filter(region -> !visitedRegions.contains(region.getName()))
                .collect(Collectors.toList());

        // 랜덤으로 섞기
        Collections.shuffle(unvisitedRegions);

        // 슬롯 리스트
        List<RouletteResponseDto.RouletteSlot> slots = new ArrayList<>();

        // 방문하지 않은 지역이 8개 이상인 경우: 지역 7개 + 아이템 1개
        // 방문하지 않은 지역이 8개 이하인 경우: 모든 미방문 지역 + 아이템으로 채우기
        int unvisitedCount = unvisitedRegions.size();
        int regionSlotCount;
        int itemSlotCount;

        if (unvisitedCount >= MAX_SLOTS) {
            // 방문하지 않은 지역이 충분히 많은 경우: 대부분 지역
            regionSlotCount = 7;
            itemSlotCount = 1;
        } else {
            // 방문하지 않은 지역이 적은 경우: 모든 미방문 지역 + 아이템
            regionSlotCount = unvisitedCount;
            itemSlotCount = MAX_SLOTS - unvisitedCount;
        }

        // 지역 슬롯 추가
        for (int i = 0; i < regionSlotCount && i < unvisitedRegions.size(); i++) {
            Region region = unvisitedRegions.get(i);
            slots.add(RouletteResponseDto.RouletteSlot.builder()
                    .type(RouletteSlotType.REGION)
                    .name(region.getName())
                    .imageUrl(region.getImageUrl())
                    .itemId(null)
                    .build());
        }

        // 아이템 슬롯 추가
        if (itemSlotCount > 0) {
            List<CharacterItem> allItems = characterItemRepository.findAll();
            Collections.shuffle(allItems);

            for (int i = 0; i < itemSlotCount && i < allItems.size(); i++) {
                CharacterItem item = allItems.get(i);
                slots.add(RouletteResponseDto.RouletteSlot.builder()
                        .type(RouletteSlotType.ITEM)
                        .name(item.getName())
                        .imageUrl(item.getImageUrl())
                        .itemId(item.getItemId())
                        .build());
            }
        }

        // 최종적으로 한 번 더 섞기
        Collections.shuffle(slots);

        return RouletteResponseDto.RouletteSlots.builder()
                .slots(slots)
                .build();
    }

    /**
     * 룰렛 스핀 (포인트 차감 및 미션 지역 또는 아이템 지급)
     * - REGION: 미션 지역 저장
     * - ITEM: 아이템 바로 지급
     */
    @Transactional
    public RouletteResponseDto.SpinResult spinRoulette(RouletteRequestDto.SpinRoulette request) {
        User user = userService.getCurrentUser();

        int rouletteCost = pointService.getPointAmount(PointActionType.ROULETTE);

        // 포인트 부족 체크
        if (user.getProfile().getPoint() < rouletteCost) {
            throw new GeneralException(ErrorStatus.INSUFFICIENT_POINTS);
        }

        // 포인트 차감
        pointService.subtractPoint(rouletteCost, PointActionType.ROULETTE);

        Roulette savedRoulette;
        LocalDateTime deadline = null;

        if (request.getType() == RouletteSlotType.REGION) {
            // 선택된 지역이 실제 존재하는 지역인지 확인
            regionRepository.findByName(request.getSelectedName())
                    .orElseThrow(() -> new GeneralException(ErrorStatus.REGION_NOT_FOUND));

            // 인증 마감일 계산 (3일 후)
            deadline = LocalDateTime.now().plusDays(3);

            // 룰렛 생성 및 저장 (미션 지역)
            Roulette roulette = Roulette.builder()
                    .user(user)
                    .type(RouletteType.TRAVEL)
                    .reward(request.getSelectedName())
                    .usedPoint(rouletteCost)
                    .earnedPoint(500)
                    .status(RouletteStatus.IN_PROGRESS)
                    .deadline(deadline)  // 마감일 저장
                    .build();

            savedRoulette = rouletteRepository.save(roulette);

        } else {  // ITEM
            // 선택된 아이템이 실제 존재하는 아이템인지 확인
            CharacterItem item = characterItemRepository.findById(request.getItemId())
                    .orElseThrow(() -> new GeneralException(ErrorStatus.ITEM_NOT_FOUND));

            // 룰렛 생성 및 저장 (아이템 지급 완료)
            Roulette roulette = Roulette.builder()
                    .user(user)
                    .type(RouletteType.GENERAL)
                    .reward(item.getName())
                    .usedPoint(rouletteCost)
                    .status(RouletteStatus.IN_PROGRESS)
                    .earnedPoint(0)  // 아이템은 포인트 보상 없음
                    .build();
            roulette.completeRouletteReward(0);  // 바로 완료 처리

            savedRoulette = rouletteRepository.save(roulette);

            // 실제 아이템을 사용자에게 지급
            // 이미 보유한 아이템인지 확인
            if (!userItemRepository.existsByUserAndItem_ItemId(user, item.getItemId())) {
                UserItem userItem = UserItem.builder()
                        .user(user)
                        .item(item)
                        .isEquipped(false)
                        .build();
                userItemRepository.save(userItem);
            }
        }

        return RouletteResponseDto.SpinResult.builder()
                .rouletteId(savedRoulette.getRouletteId())
                .reward(savedRoulette.getReward())
                .usedPoint(rouletteCost)
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
        LocalDateTime now = LocalDateTime.now();

        // 진행 중이고 마감일 이내인 룰렛 중 해당 지역과 일치하는 것 찾기
        Optional<Roulette> activeRouletteOpt = rouletteRepository
                .findActiveRouletteByUserAndRegion(user, regionName, now);

        if (activeRouletteOpt.isEmpty()) {
            return false;  // 룰렛 인증 대상 아님
        }

        Roulette roulette = activeRouletteOpt.get();

        int rouletteReward = pointService.getPointAmount(PointActionType.ROULETTE_REWARD);

        // 룰렛 인증 완료 처리 (상태 -> SUCCESS)
        roulette.completeRouletteReward(rouletteReward);

        // 보상 포인트 지급
        pointService.addPoint(user, rouletteReward, PointActionType.ROULETTE_REWARD);

        return true;
    }

    /**
     * 진행 중이고 마감일 이내인 룰렛 중 해당 지역과 일치하는 것이 있는지 확인
     */
    @Transactional(readOnly = true)
    public boolean hasActiveRoulette(User user, String regionName) {
        LocalDateTime now = LocalDateTime.now();
        return rouletteRepository
                .findActiveRouletteByUserAndRegion(user, regionName, now)
                .isPresent();
    }

    /**
     * 룰렛 내역 조회 (상태 자동 업데이트)
     */
    @Transactional
    public RouletteResponseDto.RouletteHistory getRouletteHistory() {
        User user = userService.getCurrentUser();

        List<Roulette> roulettes = rouletteRepository.findByUserOrderByCreatedAtDesc(user);

        // 각 룰렛의 상태 자동 업데이트
        roulettes.forEach(Roulette::updateStatusIfExpired);

        List<RouletteResponseDto.RouletteInfo> rouletteInfos = roulettes.stream()
                .map(roulette -> RouletteResponseDto.RouletteInfo.builder()
                        .rouletteId(roulette.getRouletteId())
                        .type(roulette.getType())
                        .reward(roulette.getReward())
                        .usedPoint(roulette.getUsedPoint())
                        .earnedPoint(roulette.getEarnedPoint())
                        .status(roulette.getStatus())
                        .createdAt(roulette.getCreatedAt())
                        .deadline(roulette.getDeadline())
                        .build())
                .collect(Collectors.toList());

        return RouletteResponseDto.RouletteHistory.builder()
                .roulettes(rouletteInfos)
                .build();
    }

    /**
     * 진행 중인 룰렛 목록 조회 (상태 자동 업데이트)
     */
    @Transactional
    public RouletteResponseDto.IncompleteRoulettes getIncompleteRoulettes() {
        User user = userService.getCurrentUser();

        List<Roulette> incompleteRoulettes = rouletteRepository.findIncompleteRoulettesByUser(user);

        // 각 룰렛의 상태 자동 업데이트
        incompleteRoulettes.forEach(Roulette::updateStatusIfExpired);

        List<RouletteResponseDto.RouletteInfo> rouletteInfos = incompleteRoulettes.stream()
                .map(roulette -> RouletteResponseDto.RouletteInfo.builder()
                        .rouletteId(roulette.getRouletteId())
                        .type(roulette.getType())
                        .reward(roulette.getReward())
                        .usedPoint(roulette.getUsedPoint())
                        .earnedPoint(roulette.getEarnedPoint())
                        .status(roulette.getStatus())
                        .createdAt(roulette.getCreatedAt())
                        .deadline(roulette.getDeadline())
                        .build())
                .collect(Collectors.toList());

        return RouletteResponseDto.IncompleteRoulettes.builder()
                .roulettes(rouletteInfos)
                .build();
    }
}

