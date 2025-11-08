package com.example.momentory.domain.user.repository;

import com.example.momentory.domain.user.entity.Follow;
import com.example.momentory.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {

    // 특정 사용자가 다른 사용자를 팔로우하고 있는지 확인
    Optional<Follow> findByFollowerAndFollowing(User follower, User following);

    // 특정 사용자를 팔로우하는 사람 수 (팔로워 수)
    Long countByFollowing(User following);

    // 특정 사용자가 팔로우하는 사람 수 (팔로잉 수)
    Long countByFollower(User follower);

    // 팔로우 여부 확인
    boolean existsByFollowerAndFollowing(User follower, User following);

    // 특정 사용자의 팔로워 목록 조회 (나를 팔로우하는 사람들)
    List<Follow> findAllByFollowing(User following);

    // 특정 사용자의 팔로잉 목록 조회 (내가 팔로우하는 사람들)
    List<Follow> findAllByFollower(User follower);
}