package com.example.momentory.domain.community.repository;

import com.example.momentory.domain.community.entity.Like;
import com.example.momentory.domain.community.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {

    // 특정 사용자(userId)가 특정 게시글(post)에 누른 좋아요가 있는지 확인합니다.
    Optional<Like> findByUserIdAndPost(Long userId, Post post);
}