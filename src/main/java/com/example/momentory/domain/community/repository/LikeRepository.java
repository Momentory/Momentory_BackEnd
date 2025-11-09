package com.example.momentory.domain.community.repository;

import com.example.momentory.domain.community.entity.Like;
import com.example.momentory.domain.community.entity.Post;
import com.example.momentory.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {

    // 특정 사용자가 특정 게시글에 누른 좋아요를 찾습니다 (활성/비활성 무관)
    Optional<Like> findByUserAndPost(User user, Post post);

    // 특정 사용자가 특정 게시글에 누른 활성화된 좋아요를 찾습니다
    Optional<Like> findByUserAndPostAndIsActiveTrue(User user, Post post);
}