package com.example.momentory.domain.map.service;

import com.example.momentory.domain.map.entity.MapMarker;
import com.example.momentory.domain.map.entity.Region;
import com.example.momentory.domain.map.repository.MapMarkerRepository;
import com.example.momentory.domain.map.repository.RegionRepository;
import com.example.momentory.domain.photo.dto.PhotoReseponseDto;
import com.example.momentory.domain.photo.entity.Photo;
import com.example.momentory.domain.photo.entity.Visibility;
import com.example.momentory.domain.photo.repository.StampRepository;
import com.example.momentory.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MapQueryService {

    private final MapMarkerRepository mapMarkerRepository;
    private final StampRepository stampRepository;
    private final RegionRepository regionRepository;

    // ========== 전체 지도용 메서드 ==========
    
    /**
     * 전체 지도용: 특정 지역의 PUBLIC 사진들만 조회 (사용자 상관없이)
     */
    public List<PhotoReseponseDto.PhotoResponse> getPublicPhotosByRegion(String regionName) {
        List<MapMarker> markers = mapMarkerRepository.findAllByRegion(regionName);

        return markers.stream()
                .map(MapMarker::getPhoto)
                .filter(photo -> photo.getVisibility() == Visibility.PUBLIC)
                .map(photo -> PhotoReseponseDto.PhotoResponse.builder()
                        .photoId(photo.getPhotoId())
                        .imageUrl(photo.getImageUrl())
                        .latitude(photo.getLatitude())
                        .longitude(photo.getLongitude())
                        .address(photo.getAddress())
                        .memo(photo.getMemo())
                        .visibility(photo.getVisibility())
                        .takenAt(photo.getTakenAt())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * 전체 지도용: 특정 지역의 최신 PUBLIC 사진 1장만 반환
     */
    public PhotoReseponseDto.PhotoResponse getLatestPublicPhotoByRegion(String regionName) {
        List<MapMarker> markers = mapMarkerRepository.findAllByRegion(regionName);
        
        Photo latestPublicPhoto = markers.stream()
                .map(MapMarker::getPhoto)
                .filter(photo -> photo.getVisibility() == Visibility.PUBLIC)
                .findFirst()
                .orElse(null);
                
        if (latestPublicPhoto == null) return null;

        return PhotoReseponseDto.PhotoResponse.builder()
                .photoId(latestPublicPhoto.getPhotoId())
                .imageUrl(latestPublicPhoto.getImageUrl())
                .latitude(latestPublicPhoto.getLatitude())
                .longitude(latestPublicPhoto.getLongitude())
                .address(latestPublicPhoto.getAddress())
                .memo(latestPublicPhoto.getMemo())
                .visibility(latestPublicPhoto.getVisibility())
                .takenAt(latestPublicPhoto.getTakenAt())
                .build();
    }

    /**
     * 전체 지도용: 모든 지역의 최신 PUBLIC 사진들을 한번에 조회
     * @return Map<String, PhotoReseponseDto.PhotoResponse> - 지역명과 최신 공개 사진
     */
    public Map<String, PhotoReseponseDto.PhotoResponse> getAllRegionsLatestPublicPhotos() {
        List<Region> allRegions = regionRepository.findAll();
        Map<String, PhotoReseponseDto.PhotoResponse> resultMap = new HashMap<>();
        
        for (Region region : allRegions) {
            PhotoReseponseDto.PhotoResponse latestPhoto = getLatestPublicPhotoByRegion(region.getName());
            if (latestPhoto != null) {
                resultMap.put(region.getName(), latestPhoto);
            }
        }
        
        return resultMap;
    }

    // ========== 나의 지도용 메서드 ==========
    
    /**
     * 나의 지도용: 특정 지역의 특정 사용자 사진들 조회 (visibility 상관없이)
     */
    public List<PhotoReseponseDto.PhotoResponse> getUserPhotosByRegion(String regionName, User user) {
        List<MapMarker> markers = mapMarkerRepository.findAllByRegion(regionName);

        return markers.stream()
                .map(MapMarker::getPhoto)
                .filter(photo -> photo.getUser().getUserId().equals(user.getUserId()))
                .map(photo -> PhotoReseponseDto.PhotoResponse.builder()
                        .photoId(photo.getPhotoId())
                        .imageUrl(photo.getImageUrl())
                        .latitude(photo.getLatitude())
                        .longitude(photo.getLongitude())
                        .address(photo.getAddress())
                        .memo(photo.getMemo())
                        .visibility(photo.getVisibility())
                        .takenAt(photo.getTakenAt())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * 나의 지도용: 특정 지역의 특정 사용자 최신 사진 1장만 반환
     */
    public PhotoReseponseDto.PhotoResponse getLatestUserPhotoByRegion(String regionName, User user) {
        List<MapMarker> markers = mapMarkerRepository.findAllByRegion(regionName);
        
        Photo latestUserPhoto = markers.stream()
                .map(MapMarker::getPhoto)
                .filter(photo -> photo.getUser().getUserId().equals(user.getUserId()))
                .findFirst()
                .orElse(null);
                
        if (latestUserPhoto == null) return null;

        return PhotoReseponseDto.PhotoResponse.builder()
                .photoId(latestUserPhoto.getPhotoId())
                .imageUrl(latestUserPhoto.getImageUrl())
                .latitude(latestUserPhoto.getLatitude())
                .longitude(latestUserPhoto.getLongitude())
                .address(latestUserPhoto.getAddress())
                .memo(latestUserPhoto.getMemo())
                .visibility(latestUserPhoto.getVisibility())
                .takenAt(latestUserPhoto.getTakenAt())
                .build();
    }

    /**
     * 나의 지도용: 모든 지역의 특정 사용자 최신 사진들을 한번에 조회
     * @param user 사용자
     * @return Map<String, PhotoReseponseDto.PhotoResponse> - 지역명과 최신 사진
     */
    public Map<String, PhotoReseponseDto.PhotoResponse> getAllRegionsLatestUserPhotos(User user) {
        List<Region> allRegions = regionRepository.findAll();
        Map<String, PhotoReseponseDto.PhotoResponse> resultMap = new HashMap<>();
        
        for (Region region : allRegions) {
            PhotoReseponseDto.PhotoResponse latestPhoto = getLatestUserPhotoByRegion(region.getName(), user);
            if (latestPhoto != null) {
                resultMap.put(region.getName(), latestPhoto);
            }
        }
        
        return resultMap;
    }

    // ========== 지역 방문 여부 확인 ==========
    
    /**
     * 현재 로그인한 사용자의 모든 지역 방문 여부를 한번에 조회
     * @param userId 현재 로그인한 사용자 ID
     * @return Map<String, Boolean> - 지역명과 방문 여부
     */
    public Map<String, Boolean> getAllRegionVisitStatus(Long userId) {
        // 1. 모든 지역 목록 조회
        List<Region> allRegions = regionRepository.findAll();
        
        // 2. 각 지역별 방문 여부 확인
        Map<String, Boolean> visitStatusMap = new HashMap<>();
        
        for (Region region : allRegions) {
            boolean hasVisited = stampRepository.existsByUserUserIdAndRegion(userId, region.getName());
            visitStatusMap.put(region.getName(), hasVisited);
        }
        
        return visitStatusMap;
    }
}
