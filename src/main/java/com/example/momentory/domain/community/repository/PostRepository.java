package com.example.momentory.domain.community.repository;

import com.example.momentory.domain.community.entity.Post;
import com.example.momentory.domain.map.entity.Region;
import com.example.momentory.domain.user.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    // 지역별 게시글 조회
    List<Post> findAllByRegion(Region region);

    // 사용자별 게시글 조회
    List<Post> findAllByUser(User user);

    // ID 목록으로 게시글 조회 (성능 최적화)
    List<Post> findByPostIdIn(List<Long> postIds);

    // 태그별 게시글 조회 (PostTag를 통해)
    @Query("SELECT DISTINCT p FROM Post p JOIN p.postTags pt JOIN pt.tag t WHERE t.name = :tagName")
    List<Post> findAllByTagName(@Param("tagName") String tagName);

    // 최신순 정렬 (전체 게시글)
    List<Post> findAllByOrderByCreatedAtDesc();

    // 지역별 최신순 정렬
    List<Post> findAllByRegionOrderByCreatedAtDesc(Region region);

    // ===== 커서 기반 페이지네이션 =====

    // 전체 게시글 커서 페이지네이션 (최신순)
    @Query("SELECT p FROM Post p WHERE p.createdAt < :cursor ORDER BY p.createdAt DESC")
    List<Post> findAllWithCursor(@Param("cursor") LocalDateTime cursor, Pageable pageable);

    // 커서가 null일 때 (첫 페이지)
    List<Post> findAllByOrderByCreatedAtDesc(Pageable pageable);

    // 지역별 게시글 커서 페이지네이션
    @Query("SELECT p FROM Post p WHERE p.region = :region AND p.createdAt < :cursor ORDER BY p.createdAt DESC")
    List<Post> findAllByRegionWithCursor(@Param("region") Region region, @Param("cursor") LocalDateTime cursor, Pageable pageable);

    // 지역별 첫 페이지
    List<Post> findAllByRegionOrderByCreatedAtDesc(Region region, Pageable pageable);

    // 태그별 게시글 커서 페이지네이션
    @Query("SELECT DISTINCT p FROM Post p JOIN p.postTags pt JOIN pt.tag t WHERE t.name = :tagName AND p.createdAt < :cursor ORDER BY p.createdAt DESC")
    List<Post> findAllByTagNameWithCursor(@Param("tagName") String tagName, @Param("cursor") LocalDateTime cursor, Pageable pageable);

    // 태그별 첫 페이지
    @Query("SELECT DISTINCT p FROM Post p JOIN p.postTags pt JOIN pt.tag t WHERE t.name = :tagName ORDER BY p.createdAt DESC")
    List<Post> findAllByTagNameOrderByCreatedAtDesc(@Param("tagName") String tagName, Pageable pageable);

    // ===== 검색 기능 (제목 + 내용) =====

    // 검색 커서 페이지네이션 (제목 또는 내용에 키워드 포함)
    @Query("SELECT p FROM Post p WHERE (p.title LIKE %:keyword% OR p.content LIKE %:keyword%) AND p.createdAt < :cursor ORDER BY p.createdAt DESC")
    List<Post> searchPostsWithCursor(@Param("keyword") String keyword, @Param("cursor") LocalDateTime cursor, Pageable pageable);

    // 검색 첫 페이지
    @Query("SELECT p FROM Post p WHERE p.title LIKE %:keyword% OR p.content LIKE %:keyword% ORDER BY p.createdAt DESC")
    List<Post> searchPostsOrderByCreatedAtDesc(@Param("keyword") String keyword, Pageable pageable);
}