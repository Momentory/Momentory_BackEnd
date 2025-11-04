package com.example.momentory.domain.community.dto;

import lombok.Getter;

public class CommentRequestDto {

    @Getter
    public static class CreateCommentDto {
        private String content;
    }

    @Getter
    public static class UpdateCommentDto {
        private String content;
    }
}