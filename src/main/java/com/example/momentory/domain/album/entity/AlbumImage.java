package com.example.momentory.domain.album.entity;

import com.example.momentory.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "album_images")
public class AlbumImage extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String imageUrl;   // S3 URL
    private String imageName;  // S3 파일 이름 (고유 key)
    private Integer displayOrder; // 이미지 표시 순서

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "album_id")
    private Album album;

    public void setAlbum(Album album) {
        this.album = album;
    }
}
