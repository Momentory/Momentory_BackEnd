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

    private String bio;
    private String externalLink;

    // === 연관관계 메서드 ===
    public void setUser(User user) {
        this.user = user;
    }

    public void updateProfile(String nickname, LocalDate birth, Gender gender, String imageName, String imageUrl, String bio, String externalLink) {
        if (nickname != null) this.nickname = nickname;
        if (birth != null) this.birth = birth;
        if (gender != null) this.gender = gender;
        if (imageName != null) this.imageName = imageName;
        if (imageUrl != null) this.imageUrl = imageUrl;
        if (bio != null) this.bio = bio;
        if (externalLink != null) this.externalLink = externalLink;
    }

    public void updateBio(String bio) {
        if (bio != null) this.bio = bio;
    }

    public void deactiveUserProfile(){
        this.imageName = null;
        this.imageUrl = null;
        this.externalLink = null;
        this.bio = null;
    }

}
