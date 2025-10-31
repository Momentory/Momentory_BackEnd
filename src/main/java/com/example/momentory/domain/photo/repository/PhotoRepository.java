package com.example.momentory.domain.photo.repository;

import com.example.momentory.domain.photo.entity.Photo;
import com.example.momentory.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface PhotoRepository extends JpaRepository<Photo,Long> {
    List<Photo> findByUserOrderByCreatedAtDesc(User user);
    List<Photo> findByUser_UserIdOrderByCreatedAtDesc(Long userId);
    
    // 커서 페이지네이션: cursor보다 이전(작은) createdAt을 가진 사진들을 조회
    @Query("SELECT p FROM Photo p WHERE p.user.userId = :userId " +
           "AND (:cursor IS NULL OR p.createdAt < :cursor) " +
           "ORDER BY p.createdAt DESC")
    List<Photo> findByUser_UserIdAndCreatedAtBeforeOrderByCreatedAtDesc(
            @Param("userId") Long userId,
            @Param("cursor") LocalDateTime cursor,
            org.springframework.data.domain.Pageable pageable);
}
