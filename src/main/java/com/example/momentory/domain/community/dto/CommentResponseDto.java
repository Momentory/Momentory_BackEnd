package com.example.momentory.domain.community.dto;

import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;

public class CommentResponseDto {

    @Getter
    @Builder
    public static class CommentDto {
        private Long commentId;
        private Long userId;        // 댓글 작성자 ID
        private String userNickname; // 댓글 작성자 닉네임
        private String content;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }
}