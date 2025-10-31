package com.example.momentory.domain.character.dto;

import com.example.momentory.domain.character.entity.CharacterType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class CharacterDto {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateRequest {
        private CharacterType characterType;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private Long characterId;
        private CharacterType characterType;
        private int level;
        private boolean isCurrentCharacter;
        private EquippedItems equippedItems;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EquippedItems {
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
    public static class CurrentCharacterResponse {
        private Long characterId;
        private CharacterType characterType;
        private int level;
        private EquippedItems equipped;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ListResponse {
        private Long characterId;
        private CharacterType characterType;
        private int level;
        private boolean isCurrentCharacter;
        private EquippedItems equippedItems;
    }

}

