package com.example.momentory.domain.user.service;

import com.example.momentory.domain.character.service.CharacterService;
import com.example.momentory.domain.notification.entity.NotificationType;
import com.example.momentory.domain.notification.event.NotificationEvent;
import com.example.momentory.domain.user.dto.UserRequestDto;
import com.example.momentory.domain.user.dto.UserResponseDto;
import com.example.momentory.domain.user.entity.Follow;
import com.example.momentory.domain.user.entity.User;
import com.example.momentory.domain.user.entity.UserProfile;
import com.example.momentory.domain.user.repository.FollowRepository;
import com.example.momentory.domain.user.repository.UserProfileRepository;
import com.example.momentory.domain.user.repository.UserRepository;
import com.example.momentory.global.code.status.ErrorStatus;
import com.example.momentory.global.exception.GeneralException;
import com.example.momentory.global.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final FollowRepository followRepository;
    private final ApplicationEventPublisher eventPublisher;

    public User getCurrentUser() {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) throw new GeneralException(ErrorStatus._UNAUTHORIZED);
        return userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));
    }

    // 내 정보 조회
    public UserResponseDto.MyInfoDto getMyInfo() {
        User user = getCurrentUser();

        UserProfile userProfile = userProfileRepository.findByUser(user).orElse(null);
        // 팔로워 수와 팔로잉 수 조회
        Long followerCount = followRepository.countByFollowing(user);
        Long followingCount = followRepository.countByFollower(user);

        return UserResponseDto.MyInfoDto.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .nickname(user.getNickname()) // User 엔티티의 nickname
                .phone(userProfile != null ? userProfile.getPhone() : null)
                .birth(userProfile != null ? userProfile.getBirth() : null)
                .gender(userProfile != null ? userProfile.getGender() : null)
                .point(userProfile != null ? userProfile.getPoint() : 0)
                .imageName(userProfile != null ? userProfile.getImageName() : null)
                .imageUrl(userProfile != null ? userProfile.getImageUrl() : null)
                .backgroundImageName(userProfile != null ? userProfile.getBackgroundImageName() : null)
                .backgroundImageUrl(userProfile != null ? userProfile.getBackgroundImageUrl() : null)
                .bio(userProfile != null ? userProfile.getBio() : null)
                .externalLink(userProfile != null ? userProfile.getExternalLink() : null)
                .followerCount(followerCount)
                .followingCount(followingCount)
                .build();
    }

    //내 정보 요약 조회
    public UserResponseDto.MySummaryInfoDto getMySummaryInfo() {
        User user = getCurrentUser();

        UserProfile userProfile = userProfileRepository.findByUser(user).orElse(null);

        return UserResponseDto.MySummaryInfoDto.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .nickname(user.getNickname())
                .imageUrl(userProfile != null ? userProfile.getImageUrl() : null)
                .build();
    }

    // 커뮤니티용 - 내 프로필 조회 (간소화)
    public UserResponseDto.CommunityProfileDto getMyCommunityProfile() {
        User user = getCurrentUser();
        UserProfile userProfile = userProfileRepository.findByUser(user).orElse(null);

        // 팔로워 수와 팔로잉 수 조회
        Long followerCount = followRepository.countByFollowing(user);
        Long followingCount = followRepository.countByFollower(user);

        return UserResponseDto.CommunityProfileDto.builder()
                .nickname(user.getNickname())
                .imageUrl(userProfile != null ? userProfile.getImageUrl() : null)
                .backgroundImageUrl(userProfile != null ? userProfile.getBackgroundImageUrl() : null)
                .bio(userProfile != null ? userProfile.getBio() : null)
                .externalLink(userProfile != null ? userProfile.getExternalLink() : null)
                .followerCount(followerCount)
                .followingCount(followingCount)
                .isFollowing(null) // 내 프로필이므로 null
                .build();
    }

    // 커뮤니티용 - 다른 사용자 프로필 조회 (간소화, 팔로우 여부 포함)
    public UserResponseDto.CommunityProfileDto getUserCommunityProfile(Long userId) {
        User currentUser = getCurrentUser();
        User targetUser = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));

        UserProfile userProfile = userProfileRepository.findByUser(targetUser).orElse(null);

        // 팔로워 수와 팔로잉 수 조회
        Long followerCount = followRepository.countByFollowing(targetUser);
        Long followingCount = followRepository.countByFollower(targetUser);

        // 팔로우 여부 확인
        boolean isFollowing = followRepository.existsByFollowerAndFollowing(currentUser, targetUser);

        return UserResponseDto.CommunityProfileDto.builder()
                .nickname(targetUser.getNickname())
                .imageUrl(userProfile != null ? userProfile.getImageUrl() : null)
                .backgroundImageUrl(userProfile != null ? userProfile.getBackgroundImageUrl() : null)
                .bio(userProfile != null ? userProfile.getBio() : null)
                .externalLink(userProfile != null ? userProfile.getExternalLink() : null)
                .followerCount(followerCount)
                .followingCount(followingCount)
                .isFollowing(isFollowing)
                .build();
    }

    // 프로필 수정
    @Transactional
    public UserResponseDto.MyInfoDto updateProfile(UserRequestDto.UpdateProfileDto request) {
        User user = getCurrentUser();

        UserProfile userProfile = userProfileRepository.findByUser(user)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_PROFILE_NOT_FOUND));

        // 프로필 업데이트 (null이 아닌 값들만 업데이트)
        userProfile.updateProfile(
                request.getNickName() != null ? request.getNickName() : userProfile.getNickname(),
                userProfile.getBirth(),
                userProfile.getGender(),
                request.getImageName() != null ? request.getImageName() : userProfile.getImageName(),
                request.getImageUrl() != null ? request.getImageUrl() : userProfile.getImageUrl(),
                request.getBackgroundImageName() != null ? request.getBackgroundImageName() : userProfile.getBackgroundImageName(),
                request.getBackgroundImageUrl() != null ? request.getBackgroundImageUrl() : userProfile.getBackgroundImageUrl(),
                request.getBio() != null ? request.getBio() : userProfile.getBio(),
                request.getExternalLink() != null ? request.getExternalLink() : userProfile.getExternalLink()
        );

        userProfileRepository.save(userProfile);

        // 팔로워 수와 팔로잉 수 조회
        Long followerCount = followRepository.countByFollowing(user);
        Long followingCount = followRepository.countByFollower(user);

        return UserResponseDto.MyInfoDto.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .nickname(user.getNickname()) // User 엔티티의 nickname
                .phone(userProfile.getPhone())
                .birth(userProfile.getBirth())
                .gender(userProfile.getGender())
                .point(userProfile.getPoint())
                .imageName(userProfile.getImageName())
                .imageUrl(userProfile.getImageUrl())
                .backgroundImageName(userProfile.getBackgroundImageName())
                .backgroundImageUrl(userProfile.getBackgroundImageUrl())
                .bio(userProfile.getBio())
                .externalLink(userProfile.getExternalLink())
                .followerCount(followerCount)
                .followingCount(followingCount)
                .build();
    }

    // 회원 탈퇴 (soft delete)
    @Transactional
    public String deleteUser() {
        User user = getCurrentUser();

        // 이미 탈퇴한 사용자인지 확인
        if (!user.isActive()) {
            throw new GeneralException(ErrorStatus.INACTIVE_USER);
        }

        // soft delete: isActive를 false로 설정하고 이름을 변경
        user.deactivateUser();
        userRepository.save(user);

        UserProfile userProfile = userProfileRepository.findByUser(user)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_PROFILE_NOT_FOUND));

        userProfile.deactivateUserProfile();
        userProfileRepository.save(userProfile);

        return "회원 탈퇴가 완료되었습니다.";
    }

    // 팔로우 토글 (등록/해제)
    @Transactional
    public boolean toggleFollow(Long currentUserId, Long targetUserId) {
        // 자기 자신을 팔로우하는 것을 방지
        if (currentUserId.equals(targetUserId)) {
            throw new GeneralException(ErrorStatus._BAD_REQUEST);
        }

        User follower = userRepository.findById(currentUserId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));

        User following = userRepository.findById(targetUserId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));

        // 팔로우 관계 확인
        Optional<Follow> existingFollow = followRepository.findByFollowerAndFollowing(follower, following);

        if (existingFollow.isPresent()) {
            // 팔로우 해제 (DELETE)
            followRepository.delete(existingFollow.get());
            return false;
        } else {
            // 팔로우 등록 (INSERT)
            Follow newFollow = Follow.builder()
                    .follower(follower)
                    .following(following)
                    .build();

            followRepository.save(newFollow);

            // 팔로우 대상자에게 알림 발송
            NotificationEvent event = NotificationEvent.builder()
                    .targetUser(following)
                    .type(NotificationType.FOLLOW)
                    .message(follower.getNickname() + "님이 회원님을 팔로우했습니다.")
                    .relatedId(follower.getId())
                    .build();
            eventPublisher.publishEvent(event);

            return true;
        }
    }

    // 내 팔로워 목록 조회 (나를 팔로우하는 사람들)
    public List<UserResponseDto.FollowUserDto> getMyFollowers() {
        User user = getCurrentUser();

        // 나를 팔로우하는 사람들 조회
        List<Follow> followers = followRepository.findAllByFollowing(user);

        return followers.stream()
                .map(follow -> {
                    User followerUser = follow.getFollower();
                    UserProfile followerProfile = userProfileRepository.findByUser(followerUser).orElse(null);

                    return UserResponseDto.FollowUserDto.builder()
                            .userId(followerUser.getId())
                            .nickname(followerUser.getNickname())
                            .imageUrl(followerProfile != null ? followerProfile.getImageUrl() : null)
                            .build();
                })
                .collect(Collectors.toList());
    }

    // 내 팔로잉 목록 조회 (내가 팔로우하는 사람들)
    public List<UserResponseDto.FollowUserDto> getMyFollowings() {
        User user = getCurrentUser();

        // 내가 팔로우하는 사람들 조회
        List<Follow> followings = followRepository.findAllByFollower(user);

        return followings.stream()
                .map(follow -> {
                    User followingUser = follow.getFollowing();
                    UserProfile followingProfile = userProfileRepository.findByUser(followingUser).orElse(null);

                    return UserResponseDto.FollowUserDto.builder()
                            .userId(followingUser.getId())
                            .nickname(followingUser.getNickname())
                            .imageUrl(followingProfile != null ? followingProfile.getImageUrl() : null)
                            .build();
                })
                .collect(Collectors.toList());
    }
}