package com.example.momentory.domain.community.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

public class CommentRequestDto {

    @Getter
    public static class CreateCommentDto {
        private String content;
    }

    @Getter
    public static class UpdateCommentDto {
        private String content;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CommentCursorRequest {
        private LocalDateTime cursor; // 마지막으로 조회한 댓글의 createdAt
        private Integer size; // 조회할 댓글 개수
    }
}