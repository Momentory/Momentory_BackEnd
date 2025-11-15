package com.example.momentory.domain.community.repository;

import com.example.momentory.domain.community.dto.PostResponseDto;
import com.example.momentory.domain.community.entity.Post;
import com.example.momentory.domain.community.entity.Scrap;
import com.example.momentory.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ScrapRepository extends JpaRepository<Scrap, Long> {

    // 1. 스크랩 토글 시 사용: 특정 User 엔티티가 특정 Post를 스크랩했는지 확인
    Optional<Scrap> findByUserAndPost(User user, Post post);

    // 2. 사용자별 목록 조회 시 사용: 특정 User 엔티티가 스크랩한 모든 목록 조회
    List<Scrap> findAllByUser(User user);

    // ===== 썸네일 전용 쿼리 (postId, imageUrl만 조회) =====

    // 특정 사용자가 스크랩한 게시글의 썸네일 정보만 조회
    @Query("SELECT new com.example.momentory.domain.community.dto.PostResponseDto$PostThumbnailDto(p.postId, p.imageUrl) " +
            "FROM Scrap s JOIN s.post p WHERE s.user = :user")
    List<PostResponseDto.PostThumbnailDto> findPostThumbnailsByUser(@Param("user") User user);

    // 특정 게시글의 모든 스크랩 삭제
    void deleteAllByPost(Post post);
}