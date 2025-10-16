package com.example.momentory.domain.photo.entity;

import com.example.momentory.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import com.example.momentory.domain.user.entity.User;

@Entity
@Table(name = "photos")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Photo extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long photoId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String imageUrl;
    private Double latitude;
    private Double longitude;
    private String address;

    @Enumerated(EnumType.STRING)
    private Visibility visibility;

    private String memo;
    private LocalDateTime takenAt;
}

