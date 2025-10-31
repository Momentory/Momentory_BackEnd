package com.example.momentory.domain.community.repository;

import com.example.momentory.domain.community.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    // 특정 게시글(postId)에 달린 모든 댓글을 조회합니다.
    List<Comment> findAllByPostPostId(Long postId);
}