package com.example.momentory.domain.album.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

public class AlbumResponseDto {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AlbumListItem {
        private Long id;
        private String title;
        private int imageCount;
        private String thumbnailUrl; // 첫 이미지 URL (없으면 null)
        private boolean isShared; // 공유 상태
        private LocalDateTime createdAt;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AlbumDetail {
        private Long id;
        private String title;
        private List<ImageItem> images;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ImageItem {
        private Long id;
        private String imageName;
        private String imageUrl;
        private Integer index; // 표시 순서
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AlbumBasicInfo {
        private Long id;
        private String title;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SharedAlbumResponse {
        private String title;
        private List<SharedImageItem> images;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SharedImageItem {
        private String imageUrl;
        private Integer index;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ShareUrlResponse {
        private String shareUrl;
    }
}


