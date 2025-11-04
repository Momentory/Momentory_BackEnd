package com.example.momentory.domain.community.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

public class CommentResponseDto {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CommentDto {
        private Long commentId;
        private Long userId;        // 댓글 작성자 ID
        private String userNickname; // 댓글 작성자 닉네임
        private String content;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CommentCursorResponse {
        private List<CommentDto> comments;
        private LocalDateTime nextCursor; // 다음 페이지를 위한 커서
        private boolean hasNext; // 다음 페이지 존재 여부
    }
}