package com.example.momentory.domain.roulette.repository;

import com.example.momentory.domain.roulette.entity.Roulette;
import com.example.momentory.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface RouletteRepository extends JpaRepository<Roulette, Long> {

    /**
     * 특정 사용자의 3일 이내 미완료 룰렛 중 특정 지역과 일치하는 것 조회
     */
    @Query("SELECT r FROM Roulette r WHERE r.user = :user " +
            "AND r.reward = :regionName " +
            "AND r.earnedPoint = 0 " +
            "AND r.createdAt >= :threeDaysAgo")
    Optional<Roulette> findActiveRouletteByUserAndRegion(
            @Param("user") User user,
            @Param("regionName") String regionName,
            @Param("threeDaysAgo") LocalDateTime threeDaysAgo
    );

    /**
     * 특정 사용자의 모든 룰렛 내역 조회
     */
    List<Roulette> findByUserOrderByCreatedAtDesc(User user);

    /**
     * 특정 사용자의 미완료 룰렛 목록 조회 (earnedPoint = 0)
     */
    @Query("SELECT r FROM Roulette r WHERE r.user = :user AND r.earnedPoint = 0 ORDER BY r.createdAt DESC")
    List<Roulette> findIncompleteRoulettesByUser(@Param("user") User user);

    /**
     * 특정 기간 내 마감되는 미완료 룰렛 조회
     */
    List<Roulette> findAllByDeadlineBetweenAndIsCompletedFalse(LocalDateTime start, LocalDateTime end);
}

