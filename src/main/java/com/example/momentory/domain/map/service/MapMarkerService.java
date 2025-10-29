package com.example.momentory.domain.map.service;

import com.example.momentory.domain.map.entity.MapMarker;
import com.example.momentory.domain.map.entity.Region;
import com.example.momentory.domain.map.entity.UserRegionColor;
import com.example.momentory.domain.map.repository.MapMarkerRepository;
import com.example.momentory.domain.map.repository.RegionRepository;
import com.example.momentory.domain.photo.entity.Photo;
import com.example.momentory.domain.photo.repository.StampRepository;
import com.example.momentory.domain.photo.service.StampService;
import com.example.momentory.domain.user.entity.User;
import com.example.momentory.global.code.status.ErrorStatus;
import com.example.momentory.global.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Log4j2
public class MapMarkerService {

    private final MapMarkerRepository mapMarkerRepository;
    private final RegionRepository regionRepository;
    private final KakaoMapService kakaoMapService;
    private final StampService stampService;
    private final StampRepository stampRepository;
    private final UserRegionColorService userRegionColorService;


    /**
     * 사진 삭제 시 해당 사진과 연결된 MapMarker도 함께 삭제
     */
    @Transactional
    public void deleteMarkerByPhoto(Photo photo) {
        mapMarkerRepository.deleteByPhoto(photo);
    }

    /**
     * 사진 업로드 시 마커 생성 및 지역 스탬프 발급
     * @return 새로 발급된 경우 지역명 반환, 아니면 null
     */
    @Transactional
    public String createMarkerAndStampWithInfo(Photo photo, User user, String color) {
        // 프론트에서 받은 address를 사용하여 지역명 추출
        if (photo.getAddress() == null || photo.getAddress().isEmpty()) return null;
        
        String regionName = extractCityName(photo.getAddress());
        if (regionName == null) return null;

        // Region 데이터베이스에서 해당 지역 조회
        Optional<Region> regionOpt = regionRepository.findByName(regionName);
        if (regionOpt.isEmpty()) {
            // 해당 지역이 데이터베이스에 없는 경우 로그만 남기고 스탬프 발급하지 않음
            log.warn("[지역 스탬프] 데이터베이스에 없는 지역: '{}' (원본 주소: '{}')", regionName, photo.getAddress());
            return null;
        }
        
        Region region = regionOpt.get();

        // 사용자의 지역별 색깔 설정 (없으면 생성, 있으면 업데이트)
        UserRegionColor savedColor = userRegionColorService.setRegionColor(user, regionName, color);
        if (savedColor == null) {
            log.warn("[지역 색깔 저장 실패] 지역 '{}'에 대한 색깔 저장 실패", regionName);
        }

        MapMarker marker = MapMarker.builder()
                .photo(photo)
                .clusterGroup(regionName)
                .build();

        mapMarkerRepository.save(marker);

        boolean granted = stampService.grantRegionalStamp(user, regionName);
        return granted ? regionName : null;
    }


    private String extractCityName(String fullName) {
        if (fullName == null || fullName.isEmpty()) {
            return null;
        }
        
        // 예: "경기도 부천시" -> "부천시"
        // 예: "부천시 원미구" -> "부천시"
        // 예: "서울특별시 중구" -> "서울특별시"
        
        String[] parts = fullName.split(" ");
        
        // "시" 또는 "군" 또는 "구"로 끝나는 부분을 찾기
        for (String part : parts) {
            if (part.endsWith("시") || part.endsWith("군") || part.endsWith("구")) {
                return part;
            }
        }
        
        // 특별시/광역시 처리
        if (fullName.contains("특별시") || fullName.contains("광역시")) {
            return fullName.split(" ")[0];
        }
        
        // 기본적으로 첫 번째 부분 반환
        return parts[0];
    }
}
