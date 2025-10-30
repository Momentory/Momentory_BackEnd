package com.example.momentory.domain.character.repository;

import com.example.momentory.domain.character.entity.Wardrobe;
import com.example.momentory.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WardrobeRepository extends JpaRepository<Wardrobe, Long> {

    List<Wardrobe> findByUser(User user);
}

