package com.example.momentory.domain.community.entity;

import com.example.momentory.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(
    name = "likes",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_like_user_post",
            columnNames = {"user_id", "post_id"}
        )
    }
)
public class Like {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 좋아요를 누른 사용자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Post 엔티티와 연결 (Like:Post = N:1)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    // Soft delete 방식: 좋아요 활성화 여부
    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private boolean isActive = true;

    /**
     * 좋아요 활성화
     */
    public void activate() {
        this.isActive = true;
    }

    /**
     * 좋아요 비활성화 (취소)
     */
    public void deactivate() {
        this.isActive = false;
    }
}
