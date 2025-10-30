package com.example.momentory.domain.character.entity;

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

    private String imageUrl;
    private int price;
    private int unlockLevel;

    public void update(String name, ItemCategory category, String imageUrl, int price, int unlockLevel) {
        this.name = name;
        this.category = category;
        this.imageUrl = imageUrl;
        this.price = price;
        this.unlockLevel = unlockLevel;
    }
}

