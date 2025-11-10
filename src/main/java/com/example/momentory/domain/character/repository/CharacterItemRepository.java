package com.example.momentory.domain.character.repository;

import com.example.momentory.domain.character.entity.CharacterItem;
import com.example.momentory.domain.character.entity.status.ItemCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CharacterItemRepository extends JpaRepository<CharacterItem, Long> {

    List<CharacterItem> findByCategory(ItemCategory category);

    List<CharacterItem> findAllByOrderByPriceAsc();

    List<CharacterItem> findTop3ByOrderByCreatedAtDesc();
}

