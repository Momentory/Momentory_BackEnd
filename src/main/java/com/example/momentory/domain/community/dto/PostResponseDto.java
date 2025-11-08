package com.example.momentory.domain.community.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

public class PostResponseDto {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PostDto {
        private Long postId;
        private Long userId;
        private String userNickname;
        private String userProfileImageUrl;
        private String userProfileImageName;
        private String title;
        private String content;
        private String imageUrl;
        private String imageName;
        private Long regionId;
        private String regionName;
        private List<String> tags;
        private int likeCount;
        private int commentCount;
        private boolean isScrapped; // 현재 사용자의 스크랩 여부
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PostSimpleDto {
        private Long postId;
        private String title;
    }

    // 게시글 썸네일용 DTO (postId와 imageUrl만)
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PostThumbnailDto {
        private Long postId;
        private String imageUrl;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PostListDto {
        private List<PostDto> posts;
        private int totalCount;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PostCursorResponse {
        private List<PostDto> posts;
        private LocalDateTime nextCursor; // 다음 페이지를 위한 커서
        private boolean hasNext; // 다음 페이지 존재 여부
    }
}