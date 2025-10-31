package com.example.momentory.domain.album.repository;

import com.example.momentory.domain.album.entity.Album;
import com.example.momentory.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlbumRepository extends JpaRepository<Album, Long> {
    List<Album> findAllByUserOrderByCreatedAtDesc(User user);
}


