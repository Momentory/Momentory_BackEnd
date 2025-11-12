package com.example.momentory.domain.roulette.repository;

import com.example.momentory.domain.roulette.entity.Roulette;
import com.example.momentory.domain.roulette.entity.RouletteStatus;
import com.example.momentory.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface RouletteRepository extends JpaRepository<Roulette, Long> {

    /**
     * 특정 사용자의 마감일 이내 진행 중인 룰렛 중 특정 지역과 일치하는 것 조회
     */
    @Query("SELECT r FROM Roulette r WHERE r.user = :user " +
            "AND r.reward = :regionName " +
            "AND r.status = 'IN_PROGRESS' " +
            "AND r.deadline >= :now")
    Optional<Roulette> findActiveRouletteByUserAndRegion(
            @Param("user") User user,
            @Param("regionName") String regionName,
            @Param("now") LocalDateTime now
    );

    /**
     * 특정 사용자의 모든 룰렛 내역 조회
     */
    List<Roulette> findByUserOrderByCreatedAtDesc(User user);

    /**
     * 특정 사용자의 진행 중인 룰렛 목록 조회
     */
    @Query("SELECT r FROM Roulette r WHERE r.user = :user AND r.status = 'IN_PROGRESS' ORDER BY r.createdAt DESC")
    List<Roulette> findIncompleteRoulettesByUser(@Param("user") User user);

    /**
     * 마감일이 지났지만 아직 FAILED로 업데이트되지 않은 룰렛 조회 (스케줄러용)
     */
    @Query("SELECT r FROM Roulette r WHERE r.status = 'IN_PROGRESS' AND r.deadline < :now")
    List<Roulette> findExpiredRoulettes(@Param("now") LocalDateTime now);
}

