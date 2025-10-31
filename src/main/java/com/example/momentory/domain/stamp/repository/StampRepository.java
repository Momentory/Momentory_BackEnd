package com.example.momentory.domain.stamp.repository;

import com.example.momentory.domain.stamp.entity.Stamp;
import com.example.momentory.domain.stamp.entity.StampType;
import com.example.momentory.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StampRepository extends JpaRepository<Stamp, Long> {
    boolean existsByUserAndRegion(User user, String region);
    Optional<Stamp> findByUserAndRegion(User user, String region);
    boolean existsByUserUserIdAndRegion(Long userId, String region);
    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END FROM Stamp s WHERE s.user = :user AND s.spotName = :spotName")
    boolean existsByUserAndSpotName(@Param("user") User user, @Param("spotName") String spotName);

    List<Stamp> findByUser(User user);
    List<Stamp> findByUserAndType(User user, StampType type);
    List<Stamp> findTop3ByUserOrderByCreatedAtDesc(User user);

}
