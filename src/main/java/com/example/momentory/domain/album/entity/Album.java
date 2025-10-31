package com.example.momentory.domain.album.entity;

import com.example.momentory.global.common.BaseEntity;
import com.example.momentory.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "albums")
public class Album extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "album_id")
    private Long id;

    private String title;         // 앨범 제목
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // 앨범 안에 들어가는 여러 이미지 (1:N)
    @OneToMany(mappedBy = "album", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<AlbumImage> images = new ArrayList<>();

    public void update(String title) {
        this.title = title;
    }

    public void addImages(List<AlbumImage> albumImages) {
        this.images.addAll(albumImages);
        albumImages.forEach(img -> img.setAlbum(this));
    }
}
