package com.example.momentory.domain.community.repository;

import com.example.momentory.domain.community.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// Post 엔티티의 ID 타입은 Long이므로 <Post, Long>으로 지정합니다.
@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    // 필요한 쿼리 메서드가 있다면 여기에 추가합니다.
}