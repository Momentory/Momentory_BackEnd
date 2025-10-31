package com.example.momentory.domain.stamp.service;

import com.example.momentory.domain.stamp.entity.CulturalStampData;
import com.example.momentory.domain.stamp.entity.Stamp;
import com.example.momentory.domain.stamp.entity.StampType;
import com.example.momentory.domain.stamp.repository.StampRepository;
import com.example.momentory.domain.user.entity.User;
import com.example.momentory.domain.user.service.UserService;
import com.example.momentory.global.code.status.ErrorStatus;
import com.example.momentory.global.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import com.example.momentory.domain.stamp.dto.StampResponseDto;

@Service
@RequiredArgsConstructor
public class StampService {

    private final StampRepository stampRepository;
    private final UserService userService;

    /**
     * 지역 스탬프 발급
     */
    @Transactional
    public boolean grantRegionalStamp(User user, String regionName) {
        boolean exists = stampRepository.existsByUserAndRegion(user, regionName);
        if (exists) return false;

        Stamp stamp = Stamp.builder()
                .user(user)
                .region(regionName)
                .type(StampType.REGIONAL)
                .issuedAt(LocalDateTime.now())
                .build();

        stampRepository.save(stamp);
        return true;
    }

    /**
     * 문화 스탬프 발급 (CulturalStampData 기반)
     */
    @Transactional
    public void grantCulturalStamp(String spotName) {
        User user = userService.getCurrentUser();

        // 이미 해당 문화 스탬프 보유 시 중복 방지
        if (stampRepository.existsByUserAndSpotName(user, spotName)) {
            throw new GeneralException(ErrorStatus.DUPLICATE_RESOURCE);
        }

        // CulturalStampData 내부에서 해당 spotName 탐색
        CulturalStampData.CulturalStamp matched = CulturalStampData.getSTAMPS().stream()
                .filter(s -> s.getName().equalsIgnoreCase(spotName))
                .findFirst()
                .orElseThrow(() -> new GeneralException(ErrorStatus.RESOURCE_NOT_FOUND));

        Stamp stamp = Stamp.builder()
                .user(user)
                .region(matched.getRegionName()) // CulturalStampData에서 지역 자동 매핑
                .spotName(matched.getName())
                .type(StampType.CULTURAL)
                .issuedAt(LocalDateTime.now())
                .build();

        stampRepository.save(stamp);
    }

    @Transactional(readOnly = true)
    public StampResponseDto.MyStampsGrouped getMyStampsGrouped() {
        User user = userService.getCurrentUser();
        List<Stamp> all = stampRepository.findByUser(user);

        List<StampResponseDto.StampInfo> regional = all.stream()
                .filter(s -> s.getType() == StampType.REGIONAL)
                .map(this::toStampInfo)
                .collect(Collectors.toList());

        List<StampResponseDto.StampInfo> cultural = all.stream()
                .filter(s -> s.getType() == StampType.CULTURAL)
                .map(this::toStampInfo)
                .collect(Collectors.toList());

        return StampResponseDto.MyStampsGrouped.builder()
                .regional(regional)
                .cultural(cultural)
                .build();
    }

    @Transactional(readOnly = true)
    public List<StampResponseDto.StampInfo> getMyStampsByType(StampType type) {
        User user = userService.getCurrentUser();
        return stampRepository.findByUserAndType(user, type).stream()
                .map(this::toStampInfo)
                .collect(Collectors.toList());
    }

    private StampResponseDto.StampInfo toStampInfo(Stamp s) {
        return StampResponseDto.StampInfo.builder()
                .stampId(s.getStampId())
                .region(s.getRegion())
                .spotName(s.getSpotName())
                .type(s.getType())
                .issuedAt(s.getIssuedAt())
                .build();
    }

    public List<StampResponseDto.StampInfo> getRecentStampsByUser(){
        User user = userService.getCurrentUser();

        return stampRepository.findTop3ByUserOrderByCreatedAtDesc(user).stream()
                .map(this::toStampInfo)
                .collect(Collectors.toList());

    }
}
