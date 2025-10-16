package com.example.momentory.domain.album.entity;

import com.example.momentory.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import java.util.*;
import com.example.momentory.domain.user.entity.User;

@Entity
@Table(name = "albums")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Album extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long albumId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String title;

    @OneToMany(mappedBy = "album", cascade = CascadeType.ALL)
    private List<AlbumPage> pages = new ArrayList<>();
}

