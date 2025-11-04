package com.example.momentory.domain.community.repository;

import com.example.momentory.domain.community.entity.Post;
import com.example.momentory.domain.community.entity.Scrap;
import com.example.momentory.domain.user.entity.User; // 🚨 User 엔티티 import

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface ScrapRepository extends JpaRepository<Scrap, Long> {

    // 1. 스크랩 토글 시 사용: 특정 User 엔티티가 특정 Post를 스크랩했는지 확인
    Optional<Scrap> findByUserAndPost(User user, Post post);

    // 2. 사용자별 목록 조회 시 사용: 특정 User 엔티티가 스크랩한 모든 목록 조회
    List<Scrap> findAllByUser(User user);
}