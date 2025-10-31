package com.example.momentory.domain.album.service;

import com.example.momentory.domain.album.dto.AlbumRequestDto;
import com.example.momentory.domain.album.dto.AlbumResponseDto;
import com.example.momentory.domain.album.entity.Album;
import com.example.momentory.domain.album.entity.AlbumImage;
import com.example.momentory.domain.album.repository.AlbumImageRepository;
import com.example.momentory.domain.album.repository.AlbumRepository;
import com.example.momentory.domain.user.entity.User;
import com.example.momentory.domain.user.service.UserService;
import com.example.momentory.global.code.status.ErrorStatus;
import com.example.momentory.global.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AlbumService {

    private final AlbumRepository albumRepository;
    private final AlbumImageRepository albumImageRepository;
    private final UserService userService;

    @Value("${app.frontend.redirect-url}")
    private String frontendRedirectUrl;

    @Transactional
    public AlbumResponseDto.AlbumBasicInfo createAlbum(AlbumRequestDto.CreateAlbum request) {
        User me = userService.getCurrentUser();

        Album album = Album.builder()
                .title(request.getTitle())
                .user(me)
                .build();

        List<AlbumImage> albumImages = new ArrayList<>();
        if (request.getImages() != null) {
            for (int i = 0; i < request.getImages().size(); i++) {
                AlbumRequestDto.ImageItem item = request.getImages().get(i);
                Integer order = item.getIndex() != null ? item.getIndex() : i;
                AlbumImage image = AlbumImage.builder()
                        .imageName(item.getImageName())
                        .imageUrl(item.getImageUrl())
                        .displayOrder(order)
                        .build();
                image.setAlbum(album);
                albumImages.add(image);
            }
        }

        album.addImages(albumImages);

        Album saved = albumRepository.save(album);
        return AlbumResponseDto.AlbumBasicInfo.builder()
                .id(saved.getId())
                .title(saved.getTitle())
                .build();
    }

    public List<AlbumResponseDto.AlbumListItem> getMyAlbums() {
        User me = userService.getCurrentUser();
        List<Album> albums = albumRepository.findAllByUserOrderByCreatedAtDesc(me);
        return albums.stream().map(a -> {
            // displayOrder 순서대로 정렬된 이미지 리스트에서 첫 번째 이미지를 썸네일로 사용
            List<AlbumImage> sortedImages = albumImageRepository.findAllByAlbumOrderByDisplayOrderAsc(a);
            String thumbnail = sortedImages.isEmpty() ? null : sortedImages.get(0).getImageUrl();
            return AlbumResponseDto.AlbumListItem.builder()
                    .id(a.getId())
                    .title(a.getTitle())
                    .imageCount(a.getImages().size())
                    .thumbnailUrl(thumbnail)
                    .isShared(a.isShared())
                    .createdAt(a.getCreatedAt())
                    .build();
        }).collect(Collectors.toList());
    }

    public AlbumResponseDto.AlbumDetail getAlbumDetail(Long albumId) {
        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.ALBUM_NOT_FOUND));

        List<AlbumImage> images = albumImageRepository.findAllByAlbumOrderByDisplayOrderAsc(album);

        List<AlbumResponseDto.ImageItem> imageItems = images.stream().map(img ->
                AlbumResponseDto.ImageItem.builder()
                        .id(img.getId())
                        .imageName(img.getImageName())
                        .imageUrl(img.getImageUrl())
                        .index(img.getDisplayOrder())
                        .build()
        ).collect(Collectors.toList());

        return AlbumResponseDto.AlbumDetail.builder()
                .id(album.getId())
                .title(album.getTitle())
                .images(imageItems)
                .createdAt(album.getCreatedAt())
                .updatedAt(album.getUpdatedAt())
                .build();
    }

    @Transactional
    public AlbumResponseDto.AlbumBasicInfo updateAlbum(Long albumId, AlbumRequestDto.UpdateAlbum request) {
        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.ALBUM_NOT_FOUND));
        
        // 제목 수정
        if (request.getTitle() != null) {
            album.update(request.getTitle());
        }
        
        // 이미지 순서 변경/추가 처리 (전체 이미지 리스트로 교체)
        if (request.getImages() != null && !request.getImages().isEmpty()) {
            // 기존 이미지 모두 삭제 후 새로운 리스트로 전체 교체
            // 프론트에서 유지할 기존 이미지 + 새로 추가할 이미지를 모두 포함하여 전달
            album.getImages().clear();
            
            // 새로운 순서로 이미지 추가 (기존 이미지 재추가 + 새 이미지 추가 모두 가능)
            List<AlbumImage> newImages = new ArrayList<>();
            for (AlbumRequestDto.ImageItem item : request.getImages()) {
                Integer order = item.getIndex() != null ? item.getIndex() : newImages.size();
                AlbumImage image = AlbumImage.builder()
                        .imageName(item.getImageName())
                        .imageUrl(item.getImageUrl())
                        .displayOrder(order)
                        .build();
                image.setAlbum(album);
                newImages.add(image);
            }
            album.addImages(newImages);
        }

        return AlbumResponseDto.AlbumBasicInfo.builder()
                .id(album.getId())
                .title(album.getTitle())
                .build();
    }

    @Transactional
    public AlbumResponseDto.ShareUrlResponse createShareLink(Long albumId) {
        User me = userService.getCurrentUser();
        
        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.ALBUM_NOT_FOUND));
        
        // 권한 확인: 앨범 소유자가 아니면 예외 발생
        if (!album.getUser().getUserId().equals(me.getUserId())) {
            throw new GeneralException(ErrorStatus.ALBUM_ACCESS_DENIED);
        }
        
        // UUID 생성 및 공유 활성화
        String shareUuid = UUID.randomUUID().toString();
        album.enableShare(shareUuid);
        albumRepository.save(album);
        
        // 프론트 주소로 공유 링크 생성
        String shareUrl = frontendRedirectUrl + "/share/" + shareUuid;
        
        return AlbumResponseDto.ShareUrlResponse.builder()
                .shareUrl(shareUrl)
                .build();
    }

    public AlbumResponseDto.SharedAlbumResponse getSharedAlbum(String shareUuid) {
        Album album = albumRepository.findByShareUuidAndIsSharedTrue(shareUuid)
                .orElseThrow(() -> new GeneralException(ErrorStatus.SHARED_ALBUM_NOT_FOUND));
        
        List<AlbumImage> images = albumImageRepository.findAllByAlbumOrderByDisplayOrderAsc(album);
        
        List<AlbumResponseDto.SharedImageItem> imageItems = images.stream().map(img ->
                AlbumResponseDto.SharedImageItem.builder()
                        .imageUrl(img.getImageUrl())
                        .index(img.getDisplayOrder())
                        .build()
        ).collect(Collectors.toList());
        
        return AlbumResponseDto.SharedAlbumResponse.builder()
                .title(album.getTitle())
                .images(imageItems)
                .build();
    }

    @Transactional
    public void unshareAlbum(Long albumId) {
        User me = userService.getCurrentUser();
        
        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.ALBUM_NOT_FOUND));
        
        // 권한 확인: 앨범 소유자가 아니면 예외 발생
        if (!album.getUser().getUserId().equals(me.getUserId())) {
            throw new GeneralException(ErrorStatus.ALBUM_ACCESS_DENIED);
        }
        
        // 공유 해제
        album.disableShare();
        albumRepository.save(album);
    }
}


