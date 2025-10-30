package com.example.momentory.domain.character.entity;

import com.example.momentory.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import com.example.momentory.domain.user.entity.User;

@Entity
@Table(name = "wardrobes")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Wardrobe extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long wardrobeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // 저장된 스타일 조합
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clothing_id")
    private UserItem clothing;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "expression_id")
    private UserItem expression;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "effect_id")
    private UserItem effect;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "decoration_id")
    private UserItem decoration;

    // 편의 메서드
    public void setUser(User user) {
        this.user = user;
    }

    public void updateStyle(UserItem clothing, UserItem expression, UserItem effect, UserItem decoration) {
        this.clothing = clothing;
        this.expression = expression;
        this.effect = effect;
        this.decoration = decoration;
    }
}

