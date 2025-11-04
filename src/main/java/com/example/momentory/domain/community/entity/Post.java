package com.example.momentory.domain.community.entity;

import com.example.momentory.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import java.util.*;
import com.example.momentory.domain.user.entity.User;
import com.example.momentory.domain.photo.entity.Photo;
import com.example.momentory.domain.map.entity.Region;
import com.example.momentory.domain.tag.entity.PostTag;

@Entity
@Table(name = "posts")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Post extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "photo_id")
    private Photo photo; // optional

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region_id")
    private Region region;

    @Column
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column
    private String imageUrl;

    @Column
    private String imageName;

    // ⭐ 좋아요 기능 추가: 좋아요 개수를 저장하는 필드
    @Builder.Default
    private int likeCount = 0;

    // ⭐ 댓글 개수를 저장하는 필드
    @Builder.Default
    private int commentCount = 0;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<PostTag> postTags = new ArrayList<>();

    // --- 좋아요 개수를 조작하는 메서드 추가 ---

    /**
     * 좋아요 개수를 1 증가시킵니다.
     */
    public void increaseLikeCount() {
        this.likeCount++;
    }

    /**
     * 좋아요 개수를 1 감소시킵니다. (최소 0 유지)
     */
    public void decreaseLikeCount() {
        if (this.likeCount > 0) {
            this.likeCount--;
        }
    }

    /**
     * 댓글 개수를 1 증가시킵니다.
     */
    public void increaseCommentCount() {
        this.commentCount++;
    }

    /**
     * 댓글 개수를 1 감소시킵니다. (최소 0 유지)
     */
    public void decreaseCommentCount() {
        if (this.commentCount > 0) {
            this.commentCount--;
        }
    }

    /**
     * 게시글 정보를 수정합니다.
     */
    public void updatePost(String title, String content, Region region, String imageUrl, String imageName) {
        if (title != null) this.title = title;
        if (content != null) this.content = content;
        if (region != null) this.region = region;
        if (imageUrl != null) this.imageUrl = imageUrl;
        if (imageName != null) this.imageName = imageName;
    }
}
