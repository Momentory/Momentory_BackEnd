package com.example.momentory.domain.photo.service;

import com.example.momentory.domain.map.service.CulturalSpotService;
import com.example.momentory.domain.map.service.MapMarkerService;
import com.example.momentory.domain.photo.converter.PhotoConverter;
import com.example.momentory.domain.photo.dto.PhotoRequestDto;
import com.example.momentory.domain.photo.dto.PhotoReseponseDto;
import com.example.momentory.domain.photo.entity.Photo;
import com.example.momentory.domain.photo.entity.Visibility;
import com.example.momentory.domain.photo.repository.PhotoRepository;
import com.example.momentory.domain.photo.service.StampService;
import com.example.momentory.domain.user.entity.User;
import com.example.momentory.domain.user.repository.UserRepository;
import com.example.momentory.global.code.status.ErrorStatus;
import com.example.momentory.global.exception.GeneralException;
import com.example.momentory.global.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Log4j2
public class PhotoService {
    private final PhotoRepository photoRepository;
    private final UserRepository userRepository;
    private final MapMarkerService mapMarkerService;
    private final CulturalSpotService culturalSpotService;
    private final com.example.momentory.domain.photo.repository.StampRepository stampRepository;

    // 포토 업로드
    @Transactional
    public PhotoReseponseDto.PhotoUploadResponse uploadPhoto(PhotoRequestDto.PhotoUpload photoRequest) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) throw new GeneralException(ErrorStatus._UNAUTHORIZED);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));

        Photo photo = PhotoConverter.uploadToPhoto(photoRequest, user);
        Photo savedPhoto = photoRepository.save(photo);

        // 지역 스탬프
        String regionalStampName = mapMarkerService.createMarkerAndStampWithInfo(savedPhoto, user);
        boolean regionalStampGranted = (regionalStampName != null);

// 근처 문화시설 (TourAPI)
        boolean hasNearbyCulturalSpots = false;
        String nearbyCulturalSpotName = null;

        if (savedPhoto.getLatitude() != null && savedPhoto.getLongitude() != null) {
            Optional<Map<String, String>> nearbySpotOpt =
                    culturalSpotService.getNearbyCulturalSpot(savedPhoto.getLatitude(), savedPhoto.getLongitude());

            if (nearbySpotOpt.isPresent()) {
                Map<String, String> spot = nearbySpotOpt.get();
                double distance = Double.parseDouble(spot.get("distance"));
                String type = spot.get("type");

                // 300m 이내일 때만 문화 스탬프 후보 인정
                if (distance <= 300) {
                    hasNearbyCulturalSpots = true;
                    nearbyCulturalSpotName = spot.get("name");
                    log.info("[문화 스탬프 후보] '{}' ({}, {}m)", nearbyCulturalSpotName, type, distance);
                } else {
                    log.info("[문화 스탬프 제외] '{}'은 {}m로 300m 초과 → 미발급", spot.get("name"), distance);
                }
            } else {
                log.info("[문화 스탬프 후보] 근처 문화시설 없음");
            }
        }

        return PhotoReseponseDto.PhotoUploadResponse.builder()
                .photoId(savedPhoto.getPhotoId())
                .imageName(savedPhoto.getImageName())
                .imageUrl(savedPhoto.getImageUrl())
                .regionalStampGranted(regionalStampGranted)
                .regionalStampName(regionalStampName)
                .hasNearbyCulturalSpots(hasNearbyCulturalSpots)
                .nearbyCulturalSpotName(nearbyCulturalSpotName)
                .build();
    }

    

    // 포토 수정
    @Transactional
    public PhotoReseponseDto.PhotoResponse updatePhoto(Long photoId, PhotoRequestDto.PhotoUpdate photoRequest) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            throw new GeneralException(ErrorStatus._UNAUTHORIZED);
        }

        Photo photo = photoRepository.findById(photoId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.RESOURCE_NOT_FOUND));

        // 본인의 포토만 수정 가능
        if (!photo.getUser().getUserId().equals(userId)) {
            throw new GeneralException(ErrorStatus._FORBIDDEN);
        }

        Visibility visibility = null;
        if (photoRequest.getVisibility() != null) {
            visibility = photoRequest.getVisibility() ? Visibility.PUBLIC : Visibility.PRIVATE;
        }

        photo.updatePhoto(
                photoRequest.getLatitude(),
                photoRequest.getLongitude(),
                photoRequest.getAddress(),
                photoRequest.getMemo(),
                visibility
        );

        return PhotoConverter.toPhotoResponse(photo);
    }

    // 포토 삭제
    @Transactional
    public void deletePhoto(Long photoId) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            throw new GeneralException(ErrorStatus._UNAUTHORIZED);
        }

        Photo photo = photoRepository.findById(photoId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.RESOURCE_NOT_FOUND));

        // 본인의 포토만 삭제 가능
        if (!photo.getUser().getUserId().equals(userId)) {
            throw new GeneralException(ErrorStatus._FORBIDDEN);
        }

        // MapMarker 삭제 (외래키 제약조건 해결)
        mapMarkerService.deleteMarkerByPhoto(photo);
        
        // Photo 삭제
        photoRepository.delete(photo);
    }

    // 포토 조회
    public PhotoReseponseDto.PhotoResponse getPhoto(Long photoId) {
        Photo photo = photoRepository.findById(photoId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.RESOURCE_NOT_FOUND));

        return PhotoConverter.toPhotoResponse(photo);
    }

    // 포토 공개 여부 변경
    @Transactional
    public PhotoReseponseDto.PhotoResponse changePhotoVisibility(Long photoId, PhotoRequestDto.VisibilityChange visibilityRequest) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            throw new GeneralException(ErrorStatus._UNAUTHORIZED);
        }

        Photo photo = photoRepository.findById(photoId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.RESOURCE_NOT_FOUND));

        // 본인의 포토만 수정 가능
        if (!photo.getUser().getUserId().equals(userId)) {
            throw new GeneralException(ErrorStatus._FORBIDDEN);
        }

        Visibility visibility = visibilityRequest.getVisibility() ? Visibility.PUBLIC : Visibility.PRIVATE;
        photo.updatePhoto(null, null, null, null, visibility);

        return PhotoConverter.toPhotoResponse(photo);
    }

    // 업로드 후 근처 관광지 추천
    public PhotoReseponseDto.NearbySpotsResponse getNearbySpots(Long photoId, Double latitude, Double longitude) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            throw new GeneralException(ErrorStatus._UNAUTHORIZED);
        }

        Photo photo = photoRepository.findById(photoId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.RESOURCE_NOT_FOUND));

        // 본인의 포토만 조회 가능
        if (!photo.getUser().getUserId().equals(userId)) {
            throw new GeneralException(ErrorStatus._FORBIDDEN);
        }

        // 위경도가 없으면 빈 리스트 반환
        if (latitude == null || longitude == null) {
            return PhotoReseponseDto.NearbySpotsResponse.builder()
                    .photoId(photoId)
                    .latitude(latitude)
                    .longitude(longitude)
                    .address(photo.getAddress())
                    .spots(new java.util.ArrayList<>())
                    .build();
        }

        // 관광지 추천 조회 (사용자가 제공한 위경도 사용)
        List<Map<String, String>> recommendedSpots = culturalSpotService.getRecommendedSpots(latitude, longitude);

        // SpotInfo 리스트로 변환
        List<PhotoReseponseDto.SpotInfo> spots = recommendedSpots.stream()
                .map(spot -> PhotoReseponseDto.SpotInfo.builder()
                        .name(spot.get("name"))
                        .type(spot.get("type"))
                        .region(spot.get("region"))
                        .address(spot.get("address"))
                        .tel(spot.get("tel"))
                        .imageUrl(spot.get("imageUrl"))
                        .build())
                .toList();

        return PhotoReseponseDto.NearbySpotsResponse.builder()
                .photoId(photoId)
                .latitude(latitude)
                .longitude(longitude)
                .address(photo.getAddress())
                .spots(spots)
                .build();
    }
}
