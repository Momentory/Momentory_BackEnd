package com.example.momentory.domain.character.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class WardrobeDto {

    @Getter
    @Builder
    @NoArgsConstructor
    public static class CreateRequest {
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private Long wardrobeId;
        private ItemInfo clothing;
        private ItemInfo expression;
        private ItemInfo effect;
        private ItemInfo decoration;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ItemInfo {
        private Long itemId;
        private String name;
        private String imageUrl;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ListResponse {
        private Long wardrobeId;
        private ItemInfo clothing;
        private ItemInfo expression;
        private ItemInfo effect;
        private ItemInfo decoration;
    }
}

