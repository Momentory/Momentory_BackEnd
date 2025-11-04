package com.example.momentory.domain.tag.repository;

import com.example.momentory.domain.tag.entity.PostTag;
import com.example.momentory.domain.community.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostTagRepository extends JpaRepository<PostTag, Long> {
    List<PostTag> findAllByPost(Post post);
    void deleteAllByPost(Post post);
}