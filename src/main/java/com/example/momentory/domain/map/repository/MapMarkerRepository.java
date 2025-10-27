package com.example.momentory.domain.map.repository;

import com.example.momentory.domain.map.entity.MapMarker;
import com.example.momentory.domain.photo.entity.Photo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MapMarkerRepository extends JpaRepository<MapMarker, Long> {

    @Query("SELECT m FROM MapMarker m JOIN FETCH m.photo p WHERE m.clusterGroup = :regionName ORDER BY p.createdAt DESC")
    List<MapMarker> findAllByRegion(@Param("regionName") String regionName);
    
    void deleteByPhoto(Photo photo);
}
