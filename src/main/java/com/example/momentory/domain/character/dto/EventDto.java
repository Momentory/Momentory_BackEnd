package com.example.momentory.domain.character.dto;

import com.example.momentory.domain.character.entity.status.EventType;
import com.example.momentory.domain.character.entity.status.ItemCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

public class EventDto {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateRequest {
        private String title;
        private String description;
        private LocalDateTime startDate;
        private LocalDateTime endDate;
        private EventType eventType;
        private boolean isActive;
        private List<EventItemRequest> items; // 이벤트와 함께 생성할 아이템 목록 (optional)
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EventItemRequest {
        private String name;
        private ItemCategory category;
        private String imageName;
        private String imageUrl;
        private int price;
        private int unlockLevel;
        private boolean isLimited; // 한정판 여부 (이벤트에 연결된 아이템은 기본적으로 한정판)
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateRequest {
        private String title;
        private String description;
        private LocalDateTime startDate;
        private LocalDateTime endDate;
        private EventType eventType;
        private boolean isActive;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private Long eventId;
        private String title;
        private String description;
        private LocalDateTime startDate;
        private LocalDateTime endDate;
        private EventType eventType;
        private boolean isActive;
        private boolean isOngoing; // 현재 진행 중 여부
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ListResponse {
        private Long eventId;
        private String title;
        private String description;
        private LocalDateTime startDate;
        private LocalDateTime endDate;
        private EventType eventType;
        private boolean isActive;
        private boolean isOngoing;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EventWithItemsResponse {
        private Long eventId;
        private String title;
        private String description;
        private LocalDateTime startDate;
        private LocalDateTime endDate;
        private EventType eventType;
        private boolean isActive;
        private boolean isOngoing;
        private List<EventItemInfo> items;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EventItemInfo {
        private Long itemId;
        private String name;
        private String imageUrl;
        private int price;
    }
}