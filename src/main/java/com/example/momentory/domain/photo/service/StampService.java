package com.example.momentory.domain.photo.service;

import com.example.momentory.domain.map.service.CulturalSpotService;
import com.example.momentory.domain.photo.entity.Stamp;
import com.example.momentory.domain.photo.entity.StampType;
import com.example.momentory.domain.photo.repository.StampRepository;
import com.example.momentory.domain.user.entity.User;
import com.example.momentory.domain.user.repository.UserRepository;
import com.example.momentory.global.code.status.ErrorStatus;
import com.example.momentory.global.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StampService {

    private final StampRepository stampRepository;
    private final UserRepository userRepository;
    private final CulturalSpotService culturalSpotService;

    /**
     * 지역 스탬프 발급 (성공 시 true 반환)
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
     * 문화 스탬프 발급 (spotName만으로 처리)
     */
    @Transactional
    public void grantCulturalStampBySpotName(Long userId, String spotName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));

        // 이미 해당 spot 스탬프가 있는 경우 중복 방지
        if (stampRepository.existsByUserAndSpotName(user, spotName)) {
            throw new GeneralException(ErrorStatus.DUPLICATE_RESOURCE);
        }

        // TourAPI로 spotName 기반 정보 조회
        Optional<Map<String, String>> infoOpt = culturalSpotService.getSpotInfoByName(spotName);
        if (infoOpt.isEmpty()) {
            throw new GeneralException(ErrorStatus.RESOURCE_NOT_FOUND);
        }

        Map<String, String> info = infoOpt.get();
        String region = info.get("region");
        String typeStr = info.get("type");

        StampType type = StampType.valueOf(typeStr);

        Stamp stamp = Stamp.builder()
                .user(user)
                .region(region)
                .spotName(spotName)
                .type(type)
                .issuedAt(LocalDateTime.now())
                .build();

        stampRepository.save(stamp);
    }
}
