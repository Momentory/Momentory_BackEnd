package com.example.momentory.domain.user.service;

import com.example.momentory.domain.point.entity.PointActionType;
import com.example.momentory.domain.point.service.PointService;
import com.example.momentory.domain.user.entity.Follow;
import com.example.momentory.domain.user.entity.User;
import com.example.momentory.domain.user.repository.FollowRepository;
import com.example.momentory.domain.user.repository.UserRepository;
import com.example.momentory.global.code.status.ErrorStatus;
import com.example.momentory.global.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FollowService {

    private final FollowRepository followRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final PointService pointService;

    /**
     * 팔로우 토글 (설정/취소) - Soft Delete 방식
     * @param targetUserId 팔로우할 대상 사용자 ID
     * @return true: 팔로우 추가됨, false: 팔로우 취소됨
     */
    @Transactional
    public boolean toggleFollow(Long targetUserId) {
        User follower = userService.getCurrentUser();

        // 팔로우 대상 사용자 조회
        User following = userRepository.findById(targetUserId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));

        // 본인을 팔로우할 수 없음
        if (follower.getUserId().equals(following.getUserId())) {
            throw new GeneralException(ErrorStatus.CANNOT_FOLLOW_YOURSELF);
        }

        // 기존 팔로우 찾기 (활성/비활성 무관)
        Optional<Follow> existingFollow = followRepository.findByFollowerAndFollowing(follower, following);

        if (existingFollow.isPresent()) {
            Follow follow = existingFollow.get();

            if (follow.isActive()) {
                // 활성화된 팔로우 -> 비활성화 (언팔로우)
                follow.deactivate();
                return false;
            } else {
                // 비활성화된 팔로우 -> 재활성화
                follow.activate();

                // ⚠️ 중요: 재활성화 시에는 포인트 지급 안 함 (중복 지급 방지)
                return true;
            }
        } else {
            // 새로운 팔로우 생성
            Follow newFollow = Follow.builder()
                    .follower(follower)
                    .following(following)
                    .isActive(true)
                    .build();

            followRepository.save(newFollow);

            // 팔로우를 받은 사용자에게 포인트 지급 (일일 20회 제한)
            int followPoints = pointService.getPointAmount(PointActionType.FOLLOW_GAINED);
            pointService.addPoint(following, followPoints, PointActionType.FOLLOW_GAINED);

            return true;
        }
    }

    /**
     * 특정 사용자를 팔로우하고 있는지 확인 (활성화된 팔로우만)
     */
    @Transactional(readOnly = true)
    public boolean isFollowing(Long targetUserId) {
        User follower = userService.getCurrentUser();
        User following = userRepository.findById(targetUserId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));

        return followRepository.findByFollowerAndFollowingAndIsActiveTrue(follower, following).isPresent();
    }
}