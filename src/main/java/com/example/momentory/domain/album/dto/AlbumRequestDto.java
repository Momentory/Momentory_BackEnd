package com.example.momentory.domain.album.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.List;

public class AlbumRequestDto {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CreateAlbum {
        private String title;
        private List<ImageItem> images; // 프론트에서 S3 업로드 후 전달
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UpdateAlbum {
        private String title;
        private List<ImageItem> images; // 순서 변경 시 포함 (optional)
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ImageItem {
        private String imageName; // S3 key
        private String imageUrl;  // S3 public url
        private Integer index;    // 표시 순서 (0부터 시작)
    }
}


