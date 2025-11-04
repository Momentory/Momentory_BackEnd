package com.example.momentory.domain.community.repository;

import com.example.momentory.domain.community.entity.Comment;
import com.example.momentory.domain.user.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    // 특정 게시글(postId)에 달린 모든 댓글을 조회합니다.
    List<Comment> findAllByPostPostId(Long postId);

    // 특정 사용자가 작성한 모든 댓글을 조회합니다.
    List<Comment> findAllByUser(User user);

    // ===== 커서 기반 페이지네이션 =====

    // 특정 게시글의 댓글 커서 페이지네이션 (최신순)
    @Query("SELECT c FROM Comment c WHERE c.post.postId = :postId AND c.createdAt < :cursor ORDER BY c.createdAt DESC")
    List<Comment> findAllByPostPostIdWithCursor(@Param("postId") Long postId, @Param("cursor") LocalDateTime cursor, Pageable pageable);

    // 커서가 null일 때 (첫 페이지)
    List<Comment> findAllByPostPostIdOrderByCreatedAtDesc(Long postId, Pageable pageable);
}