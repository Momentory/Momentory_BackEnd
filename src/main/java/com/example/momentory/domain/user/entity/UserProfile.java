package com.example.momentory.domain.user.entity;

import com.example.momentory.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "user_profiles")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class UserProfile extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long profileId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String phone;
    private String nickname;
    private LocalDate birth;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Builder.Default
    private int point=0;
    @Builder.Default
    private int level=1;
    private String imageName;
    private String imageUrl;

    // 배경 이미지
    private String backgroundImageName;
    private String backgroundImageUrl;

    private String bio;
    private String externalLink;

    // === 연관관계 메서드 ===
    public void setUser(User user) {
        this.user = user;
    }

    public void updateProfile(String nickname, LocalDate birth, Gender gender, String imageName, String imageUrl, String backgroundImageName, String backgroundImageUrl, String bio, String externalLink) {
        if (nickname != null) this.nickname = nickname;
        if (birth != null) this.birth = birth;
        if (gender != null) this.gender = gender;
        if (imageName != null) this.imageName = imageName;
        if (imageUrl != null) this.imageUrl = imageUrl;
        if (backgroundImageName != null) this.backgroundImageName = backgroundImageName;
        if (backgroundImageUrl != null) this.backgroundImageUrl = backgroundImageUrl;
        if (bio != null) this.bio = bio;
        if (externalLink != null) this.externalLink = externalLink;
    }

    public void updateBio(String bio) {
        if (bio != null) this.bio = bio;
    }

    public void deactivateUserProfile(){
        this.imageName = null;
        this.imageUrl = null;
        this.backgroundImageName = null;
        this.backgroundImageUrl = null;
        this.externalLink = null;
        this.bio = null;
    }

    public void plusPoint(int point){
        this.point += point;
    }

    public void minusPoint(int point){
        if (this.point < point) {
            throw new IllegalStateException("포인트가 부족합니다.");
        }
        this.point -= point;
    }

    public boolean hasEnoughPoints(int requiredPoints) {
        return this.point >= requiredPoints;
    }
}
