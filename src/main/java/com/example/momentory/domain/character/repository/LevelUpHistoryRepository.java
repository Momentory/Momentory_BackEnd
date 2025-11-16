package com.example.momentory.domain.character.repository;

import com.example.momentory.domain.character.entity.Character;
import com.example.momentory.domain.character.entity.LevelUpHistory;
import com.example.momentory.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LevelUpHistoryRepository extends JpaRepository<LevelUpHistory, Long> {

    /**
     * 특정 사용자가 특정 레벨에 도달한 이력이 있는지 확인
     * (캐릭터와 무관하게 사용자당 1번만 보상 지급)
     */
    boolean existsByUserAndReachedLevel(User user, int reachedLevel);

    /**
     * 특정 사용자가 특정 레벨에 도달한 이력 조회
     */
    Optional<LevelUpHistory> findByUserAndReachedLevel(User user, int reachedLevel);
}
