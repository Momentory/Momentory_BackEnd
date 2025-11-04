package com.example.momentory.domain.community.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

public class PostRequestDto {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreatePostDto {
        private String title;
        private String content;
        private String imageUrl;
        private String imageName;
        private Long regionId;
        private List<String> tags; // 태그 이름 리스트
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdatePostDto {
        private String title;
        private String content;
        private String imageUrl;
        private String imageName;
        private Long regionId;
        private List<String> tags; // 태그 이름 리스트
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PostCursorRequest {
        private LocalDateTime cursor; // 마지막으로 조회한 게시글의 createdAt
        private Integer size; // 조회할 게시글 개수
    }
}