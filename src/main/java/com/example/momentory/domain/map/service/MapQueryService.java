package com.example.momentory.domain.map.service;

import com.example.momentory.domain.map.entity.MapMarker;
import com.example.momentory.domain.map.entity.Region;
import com.example.momentory.domain.map.repository.MapMarkerRepository;
import com.example.momentory.domain.map.repository.RegionRepository;
import com.example.momentory.domain.photo.dto.PhotoReseponseDto;
import com.example.momentory.domain.photo.entity.Photo;
import com.example.momentory.domain.photo.entity.Visibility;
import com.example.momentory.domain.stamp.repository.StampRepository;
import com.example.momentory.domain.user.entity.User;
import com.example.momentory.domain.user.repository.UserRepository;
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
    private final UserRegionColorService userRegionColorService;
    private final UserRepository userRepository;

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
                        .imageName(photo.getImageName())
                        .imageUrl(photo.getImageUrl())
                        .latitude(photo.getLatitude())
                        .longitude(photo.getLongitude())
                        .address(photo.getAddress())
                        .memo(photo.getMemo())
                        .visibility(photo.getVisibility())
                        .createdAt(photo.getCreatedAt())
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
                .imageName(latestPublicPhoto.getImageName())
                .imageUrl(latestPublicPhoto.getImageUrl())
                .latitude(latestPublicPhoto.getLatitude())
                .longitude(latestPublicPhoto.getLongitude())
                .address(latestPublicPhoto.getAddress())
                .memo(latestPublicPhoto.getMemo())
                .visibility(latestPublicPhoto.getVisibility())
                .createdAt(latestPublicPhoto.getCreatedAt())
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
                .map(photo -> {
                    return PhotoReseponseDto.PhotoResponse.builder()
                            .photoId(photo.getPhotoId())
                            .imageName(photo.getImageName())
                            .imageUrl(photo.getImageUrl())
                            .latitude(photo.getLatitude())
                            .longitude(photo.getLongitude())
                            .address(photo.getAddress())
                            .memo(photo.getMemo())
                            .visibility(photo.getVisibility())
                            .createdAt(photo.getCreatedAt())
                            .build();
                })
                .collect(Collectors.toList());
    }

    /**
     * 나의 지도용: 특정 지역의 특정 사용자 최신 사진 1장만 반환
     */
    public PhotoReseponseDto.PhotoResponse getLatestUserPhotoByRegion(String regionName, User user) {
        List<MapMarker> markers = mapMarkerRepository.findAllByRegion(regionName);
        
        MapMarker latestUserMarker = markers.stream()
                .filter(marker -> marker.getPhoto().getUser().getUserId().equals(user.getUserId()))
                .findFirst()
                .orElse(null);
                
        if (latestUserMarker == null) return null;

        Photo latestUserPhoto = latestUserMarker.getPhoto();

        // 사용자의 지역별 색깔 조회 (UserRegionColor에서만)
        String regionColor = userRegionColorService.getRegionColor(user, regionName).orElse(null);

        return PhotoReseponseDto.PhotoResponse.builder()
                .photoId(latestUserPhoto.getPhotoId())
                .imageName(latestUserPhoto.getImageName())
                .imageUrl(latestUserPhoto.getImageUrl())
                .latitude(latestUserPhoto.getLatitude())
                .longitude(latestUserPhoto.getLongitude())
                .address(latestUserPhoto.getAddress())
                .memo(latestUserPhoto.getMemo())
                .visibility(latestUserPhoto.getVisibility())
                .createdAt(latestUserPhoto.getCreatedAt())
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
     * 현재 로그인한 사용자가 방문한 지역의 색깔 정보를 조회
     * @param userId 현재 로그인한 사용자 ID
     * @return Map<String, String> - 방문한 지역명과 해당 지역의 색깔
     */
    public Map<String, String> getAllRegionVisitStatus(Long userId) {
        // 1. 모든 지역 목록 조회
        List<Region> allRegions = regionRepository.findAll();
        
        // 2. 방문한 지역만 색깔과 함께 반환
        Map<String, String> visitedRegionsMap = new HashMap<>();
        
        for (Region region : allRegions) {
            boolean hasVisited = stampRepository.existsByUserUserIdAndRegion(userId, region.getName());
            if (hasVisited) {
                // 방문한 지역의 경우, UserRegionColor에서 색깔 정보 가져오기
                User user = userRepository.findById(userId).orElse(null);
                if (user != null) {
                    String color = userRegionColorService.getRegionColor(user, region.getName()).orElse(null);
                    if (color != null) {
                        visitedRegionsMap.put(region.getName(), color);
                    }
                }
            }
        }
        
        return visitedRegionsMap;
    }
}
