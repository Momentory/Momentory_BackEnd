package com.example.momentory.domain.character.entity;

import com.example.momentory.domain.character.entity.status.CharacterType;
import com.example.momentory.domain.character.entity.status.ItemCategory;
import com.example.momentory.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import com.example.momentory.domain.user.entity.User;

@Entity
@Table(name = "characters")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Character extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long characterId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CharacterType characterType;

    private int level;
    private boolean isStarter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private User owner;

    @Column(name = "is_current_character")
    @Builder.Default
    private boolean isCurrentCharacter = false;

    // 착용 중인 아이템들
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipped_clothing_id")
    private UserItem equippedClothing;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipped_expression_id")
    private UserItem equippedExpression;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipped_effect_id")
    private UserItem equippedEffect;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipped_decoration_id")
    private UserItem equippedDecoration;

    // 편의 메서드
    public void setOwner(User owner) {
        this.owner = owner;
    }

    public void setAsCurrentCharacter() {
        this.isCurrentCharacter = true;
    }

    public void unsetAsCurrentCharacter() {
        this.isCurrentCharacter = false;
    }

    public void equipItem(UserItem item) {
        switch (item.getItem().getCategory()) {
            case CLOTHING -> this.equippedClothing = item;
            case EXPRESSION -> this.equippedExpression = item;
            case EFFECT -> this.equippedEffect = item;
            case DECORATION -> this.equippedDecoration = item;
        }
    }

    public void unequipItem(ItemCategory category) {
        switch (category) {
            case CLOTHING -> this.equippedClothing = null;
            case EXPRESSION -> this.equippedExpression = null;
            case EFFECT -> this.equippedEffect = null;
            case DECORATION -> this.equippedDecoration = null;
        }
    }

    public void updateLevel(int level) {
        this.level = level;
    }
}

