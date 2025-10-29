package com.example.momentory.domain.map.service;

import com.example.momentory.domain.map.entity.Region;
import com.example.momentory.domain.map.entity.UserRegionColor;
import com.example.momentory.domain.map.repository.RegionRepository;
import com.example.momentory.domain.map.repository.UserRegionColorRepository;
import com.example.momentory.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Log4j2
public class UserRegionColorService {

    private final UserRegionColorRepository userRegionColorRepository;
    private final RegionRepository regionRepository;

    /**
     * 사용자의 지역별 색깔 설정 또는 업데이트
     */
    @Transactional
    public UserRegionColor setRegionColor(User user, String regionName, String color) {
        Optional<Region> regionOpt = regionRepository.findByName(regionName);
        
        if (regionOpt.isEmpty()) {
            log.warn("[지역 색깔 저장 실패] 지역을 찾을 수 없습니다: '{}' (사용자: {})", regionName, user.getNickname());
            return null;
        }
        
        Region region = regionOpt.get();

        Optional<UserRegionColor> existingColor = userRegionColorRepository.findByUserAndRegion(user, region);
        
        if (existingColor.isPresent()) {
            // 기존 색깔 업데이트
            UserRegionColor userRegionColor = existingColor.get();
            userRegionColor.updateColor(color);
            log.info("[지역 색깔 업데이트] 사용자: {}, 지역: {}, 색깔: {}", user.getNickname(), regionName, color);
            return userRegionColor;
        } else {
            // 새로운 색깔 생성
            UserRegionColor userRegionColor = UserRegionColor.builder()
                    .user(user)
                    .region(region)
                    .color(color)
                    .build();
            UserRegionColor saved = userRegionColorRepository.save(userRegionColor);
            log.info("[지역 색깔 생성] 사용자: {}, 지역: {}, 색깔: {}", user.getNickname(), regionName, color);
            return saved;
        }
    }

    /**
     * 사용자의 특정 지역 색깔 조회
     */
    public Optional<String> getRegionColor(User user, String regionName) {
        Optional<Region> regionOpt = regionRepository.findByName(regionName);
        
        if (regionOpt.isEmpty()) {
            log.warn("[지역 색깔 조회 실패] 지역을 찾을 수 없습니다: '{}' (사용자: {})", regionName, user.getNickname());
            return Optional.empty();
        }
        
        Region region = regionOpt.get();
        Optional<UserRegionColor> userRegionColor = userRegionColorRepository.findByUserAndRegion(user, region);
        return userRegionColor.map(UserRegionColor::getColor);
    }

    /**
     * 사용자의 모든 지역 색깔 조회
     */
    public List<UserRegionColor> getAllRegionColors(User user) {
        return userRegionColorRepository.findByUser(user);
    }

    /**
     * 사용자의 특정 지역 색깔 삭제
     */
    @Transactional
    public void deleteRegionColor(User user, String regionName) {
        Optional<UserRegionColor> userRegionColor = userRegionColorRepository.findByUserUserIdAndRegionName(
                user.getUserId(), regionName);
        if (userRegionColor.isPresent()) {
            userRegionColorRepository.delete(userRegionColor.get());
            log.info("[지역 색깔 삭제] 사용자: {}, 지역: {}", user.getNickname(), regionName);
        }
    }
}
