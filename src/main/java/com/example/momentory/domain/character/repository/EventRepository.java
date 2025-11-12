package com.example.momentory.domain.character.repository;

import com.example.momentory.domain.character.entity.Event;
import com.example.momentory.domain.character.entity.status.EventType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    // 활성화된 이벤트만 조회
    List<Event> findByIsActiveTrue();

    // 이벤트 타입별 조회
    List<Event> findByEventType(EventType eventType);

    // 현재 진행 중인 이벤트 조회 (활성화 + 기간 내)
    @Query("SELECT e FROM Event e WHERE e.isActive = true AND e.startDate <= :now AND e.endDate >= :now")
    List<Event> findActiveEventsInPeriod(@Param("now") LocalDateTime now);

    // 예정된 이벤트 조회
    @Query("SELECT e FROM Event e WHERE e.isActive = true AND e.startDate > :now ORDER BY e.startDate ASC")
    List<Event> findUpcomingEvents(@Param("now") LocalDateTime now);

    // 종료된 이벤트 조회
    @Query("SELECT e FROM Event e WHERE e.endDate < :now ORDER BY e.endDate DESC")
    List<Event> findPastEvents(@Param("now") LocalDateTime now);
}