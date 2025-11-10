package com.example.momentory.domain.character.entity;

import com.example.momentory.domain.character.entity.status.ItemCategory;
import com.example.momentory.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "character_items")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class CharacterItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long itemId;

    private String name;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private ItemCategory category;

    private String imageName;  // S3에 저장된 파일명
    private String imageUrl;   // S3 URL
    private int price;
    private int unlockLevel;

    private boolean isLimited; // 한정 여부 (true면 이벤트 기간만 노출)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    private Event event; // 연결된 이벤트 (없을 수도 있음)

    public void update(String name, ItemCategory category, String imageName, String imageUrl, int price, int unlockLevel) {
        this.name = name;
        this.category = category;
        this.imageName = imageName;
        this.imageUrl = imageUrl;
        this.price = price;
        this.unlockLevel = unlockLevel;
    }

    public void updateWithEvent(String name, ItemCategory category, String imageName, String imageUrl, int price, int unlockLevel, boolean isLimited, Event event) {
        this.name = name;
        this.category = category;
        this.imageName = imageName;
        this.imageUrl = imageUrl;
        this.price = price;
        this.unlockLevel = unlockLevel;
        this.isLimited = isLimited;
        this.event = event;
    }
}

