package com.example.momentory.domain.map.service;

import com.example.momentory.domain.map.entity.MapMarker;
import com.example.momentory.domain.map.entity.Region;
import com.example.momentory.domain.map.repository.MapMarkerRepository;
import com.example.momentory.domain.map.repository.RegionRepository;
import com.example.momentory.domain.photo.entity.Photo;
import com.example.momentory.domain.photo.repository.StampRepository;
import com.example.momentory.domain.photo.service.StampService;
import com.example.momentory.domain.user.entity.User;
import com.example.momentory.global.code.status.ErrorStatus;
import com.example.momentory.global.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MapMarkerService {

    private final MapMarkerRepository mapMarkerRepository;
    private final RegionRepository regionRepository;
    private final KakaoMapService kakaoMapService;
    private final StampService stampService;
    private final StampRepository stampRepository;

    /**
     * 사진 업로드 시 위치 정보가 있다면 자동으로 마커 생성 + 스탬프 발급
     */
    @Transactional
    public void createMarkerAndStamp(Photo photo) {
        // 위경도가 없으면 스킵
        if (photo.getLatitude() == null || photo.getLongitude() == null) return;

        // Kakao API로 행정구역 조회
        String regionFullName = kakaoMapService.getRegionName(photo.getLatitude(), photo.getLongitude());
        String regionName = extractCityName(regionFullName); // ex) "부천시 원미구" → "부천시"

        // Region 엔티티 조회
        Region region = regionRepository.findByName(regionName)
                .orElseThrow(() -> new GeneralException(ErrorStatus.RESOURCE_NOT_FOUND));

        // MapMarker 저장
        MapMarker marker = MapMarker.builder()
                .photo(photo)
                .clusterGroup(regionName)
                .build();

        mapMarkerRepository.save(marker);

        // 스탬프 발급 (중복 방지)
        stampService.grantRegionalStamp(photo.getUser(), regionName);
    }

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
    public String createMarkerAndStampWithInfo(Photo photo, User user) {
        if (photo.getLatitude() == null || photo.getLongitude() == null) return null;

        String regionFullName = kakaoMapService.getRegionName(photo.getLatitude(), photo.getLongitude());
        String regionName = extractCityName(regionFullName);

        Region region = regionRepository.findByName(regionName)
                .orElseThrow(() -> new GeneralException(ErrorStatus.RESOURCE_NOT_FOUND));

        MapMarker marker = MapMarker.builder()
                .photo(photo)
                .clusterGroup(regionName)
                .build();

        mapMarkerRepository.save(marker);

        boolean granted = stampService.grantRegionalStamp(user, regionName);
        return granted ? regionName : null;
    }


    private String extractCityName(String fullName) {
        // 예: "부천시 원미구" -> "부천시"
        if (fullName.contains(" ")) {
            return fullName.split(" ")[0];
        }
        return fullName;
    }
}
