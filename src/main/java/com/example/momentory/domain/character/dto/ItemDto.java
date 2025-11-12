package com.example.momentory.domain.character.dto;

import com.example.momentory.domain.character.entity.status.ItemCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class ItemDto {

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
        private boolean isOwned;
        private boolean isEquipped;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ShopItemResponse {
        private Long itemId;
        private String name;
        private ItemCategory category;
        private String imageUrl;
        private int price;
        private int unlockLevel;
        private boolean isOwned;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MyItemResponse {
        private Long itemId;
        private String name;
        private ItemCategory category;
        private String imageUrl;
        private boolean isEquipped;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BuyRequest {
        private Long itemId;
    }
}

