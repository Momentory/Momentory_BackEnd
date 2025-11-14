package com.example.momentory.domain.community.repository;

import com.example.momentory.domain.community.dto.PostResponseDto;
import com.example.momentory.domain.community.entity.Like;
import com.example.momentory.domain.community.entity.Post;
import com.example.momentory.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {

    // 특정 사용자가 특정 게시글에 누른 좋아요를 찾습니다 (활성/비활성 무관)
    Optional<Like> findByUserAndPost(User user, Post post);

    // ===== 썸네일 전용 쿼리 (postId, imageUrl만 조회) =====

    // 특정 사용자가 좋아요한 게시글의 썸네일 정보만 조회
    @Query("SELECT new com.example.momentory.domain.community.dto.PostResponseDto$PostThumbnailDto(p.postId, p.imageUrl) " +
            "FROM Like l JOIN l.post p " +
            "WHERE l.user = :user AND l.isActive = true")
    List<PostResponseDto.PostThumbnailDto> findPostThumbnailsByUser(@Param("user") User user);

}