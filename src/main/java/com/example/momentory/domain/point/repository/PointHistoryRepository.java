package com.example.momentory.domain.point.repository;

import com.example.momentory.domain.point.entity.PointHistory;
import com.example.momentory.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PointHistoryRepository extends JpaRepository<PointHistory, Long> {

    List<PointHistory> findByUser(User user);

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM PointHistory p WHERE p.user = :user")
    int calculateTotalPointsByUser(@Param("user") User user);
}

