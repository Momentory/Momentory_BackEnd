package com.example.momentory.domain.character.dto;

import com.example.momentory.domain.character.entity.status.ItemCategory;
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
        private String imageName;
        private String imageUrl;
        private int price;
        private int unlockLevel;
        private boolean isLimited; // 이벤트 한정 여부
        private Long eventId; // 연결할 이벤트 ID (optional)
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateRequest {
        private String name;
        private ItemCategory category;
        private String imageName;
        private String imageUrl;
        private int price;
        private int unlockLevel;
        private boolean isLimited;
        private Long eventId;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private Long itemId;
        private String name;
        private ItemCategory category;
        private String imageName;
        private String imageUrl;
        private int price;
        private int unlockLevel;
        private boolean isLimited;
        private Long eventId;
        private String eventTitle; // 이벤트명 (조회 편의성)
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ListResponse {
        private Long itemId;
        private String name;
        private ItemCategory category;
        private String imageName;
        private String imageUrl;
        private int price;
        private int unlockLevel;
        private boolean isLimited;
        private Long eventId;
        private String eventTitle;
    }
}
