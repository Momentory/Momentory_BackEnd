package com.example.momentory.domain.stamp.entity;

import com.example.momentory.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import com.example.momentory.domain.user.entity.User;

@Entity
@Table(name = "stamps")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Stamp extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long stampId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String region;    // ex) 부천시
    private String spotName;  // ex) 부천향교, 부천국제판타스틱영화제

    @Enumerated(EnumType.STRING)
    private StampType type;

    private LocalDateTime issuedAt;
}
