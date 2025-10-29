package com.example.momentory.domain.map.entity;

import com.example.momentory.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_region_colors", 
       uniqueConstraints = @UniqueConstraint(name = "uk_user_region", columnNames = {"user_id", "region_id"}))
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class UserRegionColor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region_id", nullable = false)
    private Region region;

    @Column(name = "color", nullable = false)
    private String color;  // 예: "#FFB7B7"

    // 지역별 색깔 업데이트 메서드
    public void updateColor(String color) {
        this.color = color;
    }
}
