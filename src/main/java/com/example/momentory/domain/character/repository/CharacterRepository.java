package com.example.momentory.domain.character.repository;

import com.example.momentory.domain.character.entity.Character;
import com.example.momentory.domain.character.entity.status.CharacterType;
import com.example.momentory.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CharacterRepository extends JpaRepository<Character, Long> {

    List<Character> findByOwner(User owner);

    Optional<Character> findByOwnerAndIsCurrentCharacterTrue(User owner);

    @Query("SELECT c FROM Character c WHERE c.owner = :owner AND c.isCurrentCharacter = true")
    Optional<Character> findCurrentCharacterByOwner(@Param("owner") User owner);

    boolean existsByOwnerAndCharacterType(User owner, CharacterType characterType);
}

