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
     * 팔로우 토글 (설정/취소)
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

        Optional<Follow> existingFollow = followRepository.findByFollowerAndFollowing(follower, following);

        if (existingFollow.isPresent()) {
            // 팔로우 취소 (포인트 회수는 하지 않음)
            followRepository.delete(existingFollow.get());
            return false;
        } else {
            // 팔로우 추가
            Follow newFollow = Follow.builder()
                    .follower(follower)
                    .following(following)
                    .build();

            followRepository.save(newFollow);

            // 팔로우를 받은 사용자에게 포인트 지급 (일일 20회 제한)
            int followPoints = pointService.getPointAmount(PointActionType.FOLLOW_GAINED);
            pointService.addPoint(following, followPoints, PointActionType.FOLLOW_GAINED);

            return true;
        }
    }

    /**
     * 특정 사용자를 팔로우하고 있는지 확인
     */
    @Transactional(readOnly = true)
    public boolean isFollowing(Long targetUserId) {
        User follower = userService.getCurrentUser();
        User following = userRepository.findById(targetUserId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));

        return followRepository.findByFollowerAndFollowing(follower, following).isPresent();
    }
}