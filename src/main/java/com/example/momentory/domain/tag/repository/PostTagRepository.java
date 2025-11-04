package com.example.momentory.domain.tag.repository;

import com.example.momentory.domain.tag.entity.PostTag;
import com.example.momentory.domain.community.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostTagRepository extends JpaRepository<PostTag, Long> {
    List<PostTag> findAllByPost(Post post);

    @Modifying
    @Query("DELETE FROM PostTag pt WHERE pt.post = :post")
    void deleteAllByPost(@Param("post") Post post);

    /**
     * 특정 태그 이름으로 게시글 ID 목록을 조회합니다. (성능 최적화)
     * @param tagName 태그 이름
     * @return 게시글 ID 목록
     */
    @Query("SELECT DISTINCT pt.post.postId FROM PostTag pt JOIN pt.tag t WHERE t.name = :tagName")
    List<Long> findPostIdsByTagName(@Param("tagName") String tagName);
}