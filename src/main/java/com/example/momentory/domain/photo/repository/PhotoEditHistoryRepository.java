package com.example.momentory.domain.photo.repository;

import com.example.momentory.domain.photo.entity.PhotoEditHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PhotoEditHistoryRepository extends JpaRepository<PhotoEditHistory,Long> {
}
