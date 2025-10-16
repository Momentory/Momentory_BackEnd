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
    private LocalDate birth;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private int point;
    private int level;
    private String profileImage;

    // === 연관관계 메서드 ===
    public void setUser(User user) {
        this.user = user;
    }
}
