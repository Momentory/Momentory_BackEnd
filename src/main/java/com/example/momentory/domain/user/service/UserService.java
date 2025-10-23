package com.example.momentory.domain.user.service;

import com.example.momentory.domain.user.dto.UserRequestDto;
import com.example.momentory.domain.user.dto.UserResponseDto;
import com.example.momentory.domain.user.entity.User;
import com.example.momentory.domain.user.entity.UserProfile;
import com.example.momentory.domain.user.repository.UserProfileRepository;
import com.example.momentory.domain.user.repository.UserRepository;
import com.example.momentory.global.code.status.ErrorStatus;
import com.example.momentory.global.exception.GeneralException;
import com.example.momentory.global.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;

    // 내 정보 조회
    public UserResponseDto.MyInfoDto getMyInfo() {
        Long userId = SecurityUtils.getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));

        UserProfile userProfile = userProfileRepository.findByUser(user).orElse(null);

        return UserResponseDto.MyInfoDto.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .nickname(user.getNickname()) // User 엔티티의 nickname
                .phone(userProfile != null ? userProfile.getPhone() : null)
                .birth(userProfile != null ? userProfile.getBirth() : null)
                .gender(userProfile != null ? userProfile.getGender() : null)
                .point(userProfile != null ? userProfile.getPoint() : 0)
                .level(userProfile != null ? userProfile.getLevel() : 0)
                .imageName(userProfile != null ? userProfile.getImageName() : null)
                .imageUrl(userProfile != null ? userProfile.getImageUrl() : null)
                .bio(userProfile != null ? userProfile.getBio() : null)
                .externalLink(userProfile != null ? userProfile.getExternalLink() : null)
                .build();
    }

    // 다른 사용자 프로필 조회
    public UserResponseDto.ProfileDto getUserProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));

        UserProfile userProfile = userProfileRepository.findByUser(user).orElse(null);

        return UserResponseDto.ProfileDto.builder()
                .userId(user.getId())
                .nickname(user.getNickname())
                .gender(userProfile != null ? userProfile.getGender() : null)
                .point(userProfile != null ? userProfile.getPoint() : 0)
                .imageName(userProfile != null ? userProfile.getImageName() : null)
                .imageUrl(userProfile != null ? userProfile.getImageUrl() : null)
                .bio(userProfile != null ? userProfile.getBio() : null)
                .externalLink(userProfile != null ? userProfile.getExternalLink() : null)
                .build();
    }

    // 프로필 수정
    @Transactional
    public UserResponseDto.MyInfoDto updateProfile(UserRequestDto.UpdateProfileDto request) {
        Long userId = SecurityUtils.getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));

        UserProfile userProfile = userProfileRepository.findByUser(user)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_PROFILE_NOT_FOUND));

        // 프로필 업데이트 (null이 아닌 값들만 업데이트)
        userProfile.updateProfile(
                request.getNickName() != null ? request.getNickName() : userProfile.getNickname(),
                userProfile.getBirth(),
                userProfile.getGender(),
                request.getImageName() != null ? request.getImageName() : userProfile.getImageName(),
                request.getImageUrl() != null ? request.getImageUrl() : userProfile.getImageUrl(),
                request.getBio() != null ? request.getBio() : userProfile.getBio(),
                request.getExternalLink() != null ? request.getExternalLink() : userProfile.getExternalLink()
        );

        // bio 업데이트
        if (request.getBio() != null) {
            userProfile.updateBio(request.getBio());
        }

        userProfileRepository.save(userProfile);

        return UserResponseDto.MyInfoDto.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .nickname(user.getNickname()) // User 엔티티의 nickname
                .phone(userProfile.getPhone())
                .birth(userProfile.getBirth())
                .gender(userProfile.getGender())
                .point(userProfile.getPoint())
                .level(userProfile.getLevel())
                .imageName(userProfile.getImageName())
                .imageUrl(userProfile.getImageUrl())
                .bio(userProfile.getBio())
                .externalLink(userProfile.getExternalLink())
                .build();
    }

    // 회원 탈퇴 (soft delete)
    @Transactional
    public String deleteUser() {
        Long userId = SecurityUtils.getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));

        // 이미 탈퇴한 사용자인지 확인
        if (!user.isActive()) {
            throw new GeneralException(ErrorStatus.INACTIVE_USER);
        }

        // soft delete: isActive를 false로 설정하고 이름을 변경
        user.deactivateUser();
        userRepository.save(user);

        UserProfile userProfile = userProfileRepository.findByUser(user)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_PROFILE_NOT_FOUND));

        userProfile.deactiveUserProfile();
        userProfileRepository.save(userProfile);

        return "회원 탈퇴가 완료되었습니다.";
    }
}