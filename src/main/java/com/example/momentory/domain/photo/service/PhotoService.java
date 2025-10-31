package com.example.momentory.domain.photo.service;

import com.example.momentory.domain.map.service.CulturalSpotService;
import com.example.momentory.domain.map.service.MapMarkerService;
import com.example.momentory.domain.map.service.KakaoMapService;
import com.example.momentory.domain.photo.converter.PhotoConverter;
import com.example.momentory.domain.photo.dto.PhotoRequestDto;
import com.example.momentory.domain.photo.dto.PhotoReseponseDto;
import com.example.momentory.domain.photo.entity.Photo;
import com.example.momentory.domain.photo.entity.Visibility;
import com.example.momentory.domain.photo.repository.PhotoRepository;
import com.example.momentory.domain.stamp.repository.StampRepository;
import com.example.momentory.domain.user.entity.User;
import com.example.momentory.domain.user.service.UserService;
import com.example.momentory.global.code.status.ErrorStatus;
import com.example.momentory.global.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Log4j2
public class PhotoService {
    private final PhotoRepository photoRepository;
    private final UserService userService;
    private final MapMarkerService mapMarkerService;
    private final CulturalSpotService culturalSpotService;
    private final KakaoMapService kakaoMapService;
    private final StampRepository stampRepository;

    // 포토 업로드
    @Transactional
    public PhotoReseponseDto.PhotoUploadResponse uploadPhoto(PhotoRequestDto.PhotoUpload photoRequest) {
        User user = userService.getCurrentUser();

        Photo photo = PhotoConverter.uploadToPhoto(photoRequest, user);
        Photo savedPhoto = photoRepository.save(photo);

        // 지역 스탬프 (프론트에서 받은 address와 color 사용)
        String regionalStampName = mapMarkerService.createMarkerAndStampWithInfo(savedPhoto, user, photoRequest.getColor());
        boolean regionalStampGranted = (regionalStampName != null);

        // 근처 문화시설 검색 (TourAPI)
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
                }
            }
        }


        user.getProfile().plusPoint(50); //사진 업로드시 50p 추가

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
        User user = userService.getCurrentUser();

        Photo photo = photoRepository.findById(photoId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.RESOURCE_NOT_FOUND));

        // 본인의 포토만 수정 가능
        if (!photo.getUser().getUserId().equals(user.getUserId())) {
            throw new GeneralException(ErrorStatus._FORBIDDEN);
        }

        Visibility visibility = null;
        if (photoRequest.getVisibility() != null) {
            visibility = photoRequest.getVisibility() ? Visibility.PUBLIC : Visibility.PRIVATE;
        }

        photo.updatePhoto(
                photoRequest.getAddress(),
                photoRequest.getMemo(),
                visibility
        );

        return PhotoConverter.toPhotoResponse(photo);
    }

    // 포토 삭제
    @Transactional
    public void deletePhoto(Long photoId) {
        User user = userService.getCurrentUser();

        Photo photo = photoRepository.findById(photoId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.RESOURCE_NOT_FOUND));

        // 본인의 포토만 삭제 가능
        if (!photo.getUser().getUserId().equals(user.getUserId())) {
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

    // 내 사진 목록 조회 (커서 페이지네이션)
    public PhotoReseponseDto.MyPhotosCursorResponse getMyPhotos(PhotoRequestDto.MyPhotosCursorRequest request) {
        User user = userService.getCurrentUser();

        // 요청 크기 검증 및 설정 (기본값 20, 최대 50)
        int size = (request.getSize() != null && request.getSize() > 0) 
                ? Math.min(request.getSize(), 50) 
                : 20;
        
        LocalDateTime cursor = request.getCursor();
        
        // size + 1개를 조회하여 다음 페이지 존재 여부 확인
        Pageable pageable = PageRequest.of(0, size + 1);
        List<Photo> photos = photoRepository.findByUser_UserIdAndCreatedAtBeforeOrderByCreatedAtDesc(
                user.getUserId(), cursor, pageable);
        
        // hasNext 확인 (size + 1개 조회했는데 실제로 size + 1개가 있으면 다음 페이지 존재)
        boolean hasNext = photos.size() > size;
        
        // 실제 반환할 데이터는 size개만
        List<Photo> resultPhotos = hasNext ? photos.subList(0, size) : photos;
        
        // 다음 커서 계산 (마지막 항목의 createdAt, hasNext가 true일 때만 설정)
        LocalDateTime nextCursor = null;
        if (hasNext && !resultPhotos.isEmpty()) {
            nextCursor = resultPhotos.get(resultPhotos.size() - 1).getCreatedAt();
        }
        
        List<PhotoReseponseDto.PhotoResponse> photoResponses = resultPhotos.stream()
                .map(PhotoConverter::toPhotoResponse)
                .toList();
        
        return PhotoReseponseDto.MyPhotosCursorResponse.builder()
                .photos(photoResponses)
                .nextCursor(nextCursor)
                .hasNext(hasNext)
                .build();
    }

    // 포토 공개 여부 변경
    @Transactional
    public PhotoReseponseDto.PhotoResponse changePhotoVisibility(Long photoId, PhotoRequestDto.VisibilityChange visibilityRequest) {
        User user = userService.getCurrentUser();

        Photo photo = photoRepository.findById(photoId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.RESOURCE_NOT_FOUND));

        // 본인의 포토만 수정 가능
        if (!photo.getUser().getUserId().equals(user.getUserId())) {
            throw new GeneralException(ErrorStatus._FORBIDDEN);
        }

        Visibility visibility = visibilityRequest.getVisibility() ? Visibility.PUBLIC : Visibility.PRIVATE;
        photo.updatePhoto(null, null, visibility);

        return PhotoConverter.toPhotoResponse(photo);
    }

    // 업로드 후 근처 관광지 추천
    public PhotoReseponseDto.NearbySpotsResponse getNearbySpots(Long photoId, int limit) {
        User user = userService.getCurrentUser();

        Photo photo = photoRepository.findById(photoId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.RESOURCE_NOT_FOUND));

//        // 본인의 포토만 조회 가능
//        if (!photo.getUser().getUserId().equals(user.getUserId())) {
//            throw new GeneralException(ErrorStatus._FORBIDDEN);
//        }

        // 사진에서 위도/경도 추출, 없으면 기본값 사용
        Double latitude = photo.getLatitude();
        Double longitude = photo.getLongitude();
        
        if (latitude == null || longitude == null) {
            latitude = 37.486066145252344;  // 기본 위도
            longitude = 126.80233355098368;  // 기본 경도
        }

        // 지역명 추출
        String regionName = extractCityName(photo.getAddress());

        // 관광지 추천 조회 (요청 개수만 TourAPI에 요청하고, 개수 제한 적용)
        if (limit <= 0) {
            limit = 4;
        }
        List<Map<String, String>> recommendedSpots = culturalSpotService.getRecommendedSpots(latitude, longitude, limit);

        // SpotInfo 리스트로 변환
        List<PhotoReseponseDto.SpotInfo> spots = recommendedSpots.stream()
                .limit(limit)
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
                .regionName(regionName)
                .spots(spots)
                .build();
    }

    // 위도/경도로 주소 변환
    public PhotoReseponseDto.LocationToAddressResponse convertLocationToAddress(PhotoRequestDto.LocationToAddressRequest request) {
        try {
            // 카카오맵 API로 주소 조회
            String regionFullName = kakaoMapService.getRegionName(request.getLatitude(), request.getLongitude());
            String cityName = extractCityName(regionFullName);
            
            return PhotoReseponseDto.LocationToAddressResponse.builder()
                    .latitude(request.getLatitude())
                    .longitude(request.getLongitude())
                    .address(regionFullName)
                    .cityName(cityName)
                    .build();
        } catch (Exception e) {
            log.error("위치 정보 변환 실패: {}", e.getMessage());
            throw new GeneralException(ErrorStatus._INTERNAL_SERVER_ERROR);
        }
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
