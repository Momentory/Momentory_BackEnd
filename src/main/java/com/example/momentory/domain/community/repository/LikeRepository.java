package com.example.momentory.domain.community.repository;

import com.example.momentory.domain.community.entity.Like;
import com.example.momentory.domain.community.entity.Post;
import com.example.momentory.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {

    // 특정 사용자가 특정 게시글에 누른 좋아요가 있는지 확인합니다.
    Optional<Like> findByUserAndPost(User user, Post post);

    // 특정 사용자가 좋아요한 모든 게시글 조회
    List<Like> findAllByUser(User user);
}