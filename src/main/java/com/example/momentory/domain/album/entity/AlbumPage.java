package com.example.momentory.domain.album.entity;

import com.example.momentory.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import com.example.momentory.domain.photo.entity.Photo;

@Entity
@Table(name = "album_pages")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class AlbumPage extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "album_id")
    private Album album;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "photo_id")
    private Photo photo;

    private String memo;
    private int orderIndex;
}

