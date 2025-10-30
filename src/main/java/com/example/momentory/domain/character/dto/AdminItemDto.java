package com.example.momentory.domain.character.dto;

import com.example.momentory.domain.character.entity.ItemCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class AdminItemDto {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateRequest {
        private String name;
        private ItemCategory category;
        private String imageUrl;
        private int price;
        private int unlockLevel;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateRequest {
        private String name;
        private ItemCategory category;
        private String imageUrl;
        private int price;
        private int unlockLevel;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private Long itemId;
        private String name;
        private ItemCategory category;
        private String imageUrl;
        private int price;
        private int unlockLevel;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ListResponse {
        private Long itemId;
        private String name;
        private ItemCategory category;
        private String imageUrl;
        private int price;
        private int unlockLevel;
    }
}
