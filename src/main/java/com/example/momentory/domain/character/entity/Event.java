package com.example.momentory.domain.character.entity;

import com.example.momentory.domain.character.entity.status.EventType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "events")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long eventId;

    private String title; // 이벤트명 (예: 크리스마스 한정 이벤트)
    private String description;

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    @Enumerated(EnumType.STRING)
    private EventType eventType; // 예: ITEM_RELEASE, POINT_BOOST, CHALLENGE 등

    private boolean isActive; // 이벤트 활성화 여부

    // 비즈니스 메서드
    public void update(String title, String description, LocalDateTime startDate, LocalDateTime endDate, EventType eventType, boolean isActive) {
        this.title = title;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.eventType = eventType;
        this.isActive = isActive;
    }

    public boolean isEventPeriod(LocalDateTime now) {
        return isActive &&
               !now.isBefore(startDate) &&
               !now.isAfter(endDate);
    }
}
