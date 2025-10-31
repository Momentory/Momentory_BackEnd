package com.example.momentory.domain.album.repository;

import com.example.momentory.domain.album.entity.Album;
import com.example.momentory.domain.album.entity.AlbumImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlbumImageRepository extends JpaRepository<AlbumImage, Long> {
    List<AlbumImage> findAllByAlbumOrderByDisplayOrderAsc(Album album);
}


