package com.example.momentory.domain.point.service;

import com.example.momentory.domain.character.dto.CharacterDto;
import com.example.momentory.domain.character.entity.Character;
import com.example.momentory.domain.character.repository.CharacterRepository;
import com.example.momentory.domain.character.service.CharacterService;
import com.example.momentory.domain.point.dto.PointResponse;
import com.example.momentory.domain.point.entity.PointActionType;
import com.example.momentory.domain.point.entity.PointHistory;
import com.example.momentory.domain.point.repository.PointHistoryRepository;
import com.example.momentory.domain.user.entity.User;
import com.example.momentory.domain.user.service.UserService;
import com.example.momentory.global.exception.GeneralException;
import com.example.momentory.global.code.status.ErrorStatus;
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
    private final CharacterRepository characterRepository;

    // 포인트 액션별 포인트 값
    private static final int SIGNUP_POINTS = 500;          // 회원가입 웰컴 포인트
    private static final int UPLOAD_POINTS = 50;           // 사진 업로드
    private static final int RECEIVE_LIKE_POINTS = 5;      // 좋아요 받을 때
    private static final int ROULETTE_COST = 200;          // 룰렛 실행 비용
    private static final int LEVELUP_POINTS = 200;         // 레벨업 보상
    private static final int FOLLOW_GAINED_POINTS = 10;    // 팔로워 증가 시
    private static final int ROULETTE_REWARD_POINTS = 500; // 룰렛 인증 보상

    // 일일 제한 횟수
    private static final int UPLOAD_DAILY_LIMIT = 3;
    private static final int LIKE_DAILY_LIMIT = 50;
    private static final int FOLLOW_DAILY_LIMIT = 20;

    public PointResponse.CharacterPoint getUserPoint(){
        User user = userService.getCurrentUser();
        int totalPoints = pointHistoryRepository.calculateTotalPointsByUser(user);
        // CharacterRepository로 직접 조회
        Character currentCharacter = characterRepository.findCurrentCharacterByOwner(user)
                .orElseThrow(() -> new GeneralException(ErrorStatus.CURRENT_CHARACTER_NOT_FOUND));

        int level = currentCharacter.getLevel();
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
        if (amount <= 0) throw new GeneralException(ErrorStatus.INVALID_INPUT);

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

//        // 캐릭터 레벨 자동 갱신
//        characterService.updateCharacterLevel(
//                characterService.getCurrentCharacter(), user
//        );
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
        if (amount <= 0) throw new GeneralException(ErrorStatus.INVALID_INPUT);

        if (user.getProfile().getPoint() < amount) {
            throw new GeneralException(ErrorStatus.INSUFFICIENT_POINTS);
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

    /**
     * 액션 타입별 포인트 값 반환
     * @param actionType 포인트 액션 타입
     * @return 포인트 값 (획득: 양수, 차감: 해당 값을 subtractPoint에 전달, 0: 가변적이므로 직접 전달 필요)
     */
    public int getPointAmount(PointActionType actionType) {
        return switch (actionType) {
            case SIGNUP -> SIGNUP_POINTS;                   // +500p
            case UPLOAD -> UPLOAD_POINTS;                   // +50p
            case RECEIVE_LIKE -> RECEIVE_LIKE_POINTS;       // +5p
            case ROULETTE -> ROULETTE_COST;                 // 200p (차감용, subtractPoint에 전달)
            case LEVELUP -> LEVELUP_POINTS;                 // +200p
            case FOLLOW_GAINED -> FOLLOW_GAINED_POINTS;     // +10p
            case ROULETTE_REWARD -> ROULETTE_REWARD_POINTS; // +500p
            case BUY_ITEM -> 0;  // 아이템에 따라 가변적이므로 직접 amount 전달 필요
            case EDIT_USE -> 0;  // 편집 기능은 현재 포인트 미지급 (향후 정책 변경 가능)
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
