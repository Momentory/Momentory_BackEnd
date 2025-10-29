package com.example.momentory.domain.map.repository;

import com.example.momentory.domain.map.entity.Region;
import com.example.momentory.domain.map.entity.UserRegionColor;
import com.example.momentory.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRegionColorRepository extends JpaRepository<UserRegionColor, Long> {
    
    // 사용자와 지역으로 색깔 조회
    Optional<UserRegionColor> findByUserAndRegion(User user, Region region);
    
    // 사용자와 지역으로 색깔 존재 여부 확인
    boolean existsByUserAndRegion(User user, Region region);
    
    // 사용자의 모든 지역 색깔 조회
    List<UserRegionColor> findByUser(User user);
    
    // 사용자 ID와 지역 ID로 색깔 조회
    Optional<UserRegionColor> findByUserUserIdAndRegionId(Long userId, Long regionId);
    
    // 사용자 ID와 지역명으로 색깔 조회
    Optional<UserRegionColor> findByUserUserIdAndRegionName(Long userId, String regionName);
}
