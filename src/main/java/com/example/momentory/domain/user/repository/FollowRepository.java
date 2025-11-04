package com.example.momentory.domain.user.repository;

import com.example.momentory.domain.user.entity.Follow;
import com.example.momentory.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {

    // 특정 사용자가 다른 사용자를 팔로우하고 있는지 확인
    Optional<Follow> findByFollowerAndFollowing(User follower, User following);
}