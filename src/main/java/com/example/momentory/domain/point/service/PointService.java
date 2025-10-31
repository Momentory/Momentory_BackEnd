package com.example.momentory.domain.point.service;

import com.example.momentory.domain.character.dto.CharacterDto;
import com.example.momentory.domain.character.service.CharacterService;
import com.example.momentory.domain.point.dto.PointResponse;
import com.example.momentory.domain.point.entity.PointActionType;
import com.example.momentory.domain.point.entity.PointHistory;
import com.example.momentory.domain.point.repository.PointHistoryRepository;
import com.example.momentory.domain.user.entity.User;
import com.example.momentory.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class PointService {

    private final UserService userService;
    private final PointHistoryRepository pointHistoryRepository;
    private final CharacterService characterService;

    private static final int UPLOAD_DAILY_LIMIT = 3;
    private static final int LIKE_DAILY_LIMIT = 50;
    private static final int FOLLOW_DAILY_LIMIT = 20;

    public PointResponse.CharacterPoint getUserPoint(){
        User user = userService.getCurrentUser();
        int totalPoints = pointHistoryRepository.calculateTotalPointsByUser(user);
        int level = characterService.getCurrentCharacter().getLevel();
        PointResponse.UserPoint userPoint = new PointResponse.UserPoint(user.getProfile().getPoint(), totalPoints);
        return PointResponse.CharacterPoint.builder()
                .level(level)
                .userPoint(userPoint)
                .build();
    }

    @Transactional
    public void addPoint(int amount, PointActionType actionType) {
        User user = userService.getCurrentUser();
        addPoint(user, amount, actionType);
    }

    /**
     * 포인트 추가 (획득 시)
     */
    @Transactional
    public void addPoint(User user, int amount, PointActionType actionType) {
        if (amount <= 0) throw new IllegalArgumentException("amount는 0보다 커야 합니다.");

        if (isDailyLimitedAction(actionType)) {
            LocalDateTime startOfDay = LocalDateTime.now().toLocalDate().atStartOfDay();
            LocalDateTime endOfDay = startOfDay.plusDays(1);

            int todayCount = pointHistoryRepository.countTodayAction(user, actionType, startOfDay, endOfDay);
            int limit = getDailyLimit(actionType);

            if (todayCount >= limit) {
                // 제한 도달 시 포인트 미지급
                log.info("[포인트 제한] user={}, action={}, todayCount={}, limit={}",
                        user.getUserId(), actionType, todayCount, limit);
                return;
            }
        }

        // 현재 포인트 증가
        user.getProfile().plusPoint(amount);

        // 히스토리 저장
        PointHistory history = PointHistory.builder()
                .user(user)
                .amount(amount)
                .actionType(actionType)
                .build();
        pointHistoryRepository.save(history);

        // 캐릭터 레벨 자동 갱신
        characterService.updateCharacterLevel(
                characterService.getCurrentCharacter(), user
        );
    }

    public void subtractPoint(int amount, PointActionType actionType) {
        User user = userService.getCurrentUser();
        subtractPoint(user, amount, actionType);
    }

    /**
     * 포인트 차감 (소비 시)
     */
    @Transactional
    public void subtractPoint(User user, int amount, PointActionType actionType) {
        if (amount <= 0) throw new IllegalArgumentException("amount는 0보다 커야 합니다.");

        if (user.getProfile().getPoint() < amount) {
            throw new IllegalArgumentException("포인트가 부족합니다.");
        }

        // 현재 포인트 차감
        user.getProfile().minusPoint(amount);

        // 히스토리 저장
        PointHistory history = PointHistory.builder()
                .user(user)
                .amount(-amount)
                .actionType(actionType)
                .build();
        pointHistoryRepository.save(history);

        // 소비는 레벨 변화에 영향 없음 (누적포인트는 감소 안 함)
    }

    // 하루 제한이 있는 행동인지 판별
    private boolean isDailyLimitedAction(PointActionType actionType) {
        return switch (actionType) {
            case UPLOAD, RECEIVE_LIKE, FOLLOW_GAINED -> true;
            default -> false;
        };
    }

    // 행동별 하루 제한 개수 반환
    private int getDailyLimit(PointActionType actionType) {
        return switch (actionType) {
            case UPLOAD -> UPLOAD_DAILY_LIMIT;
            case RECEIVE_LIKE -> LIKE_DAILY_LIMIT;
            case FOLLOW_GAINED -> FOLLOW_DAILY_LIMIT;
            default -> Integer.MAX_VALUE;
        };
    }

    /*
     * 포인트 사용/획득 내역 조회
     */
    public PointResponse.PointHistory getPointHistory() {
        User user = userService.getCurrentUser();
        List<PointHistory> pointHistories = pointHistoryRepository.findByUser(user);

        // DTO 변환
        List<PointResponse.PointInfo> pointInfoList = pointHistories.stream()
                .map(history -> PointResponse.PointInfo.builder()
                        .amount(history.getAmount())
                        .action(history.getActionType())
                        .actionDesc(history.getActionType().getDescription())
                        .createdAt(history.getCreatedAt())
                        .build())
                .toList();

        return PointResponse.PointHistory.builder()
                .points(pointInfoList)
                .build();
    }


}
