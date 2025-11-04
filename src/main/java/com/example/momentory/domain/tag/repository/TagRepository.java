package com.example.momentory.domain.tag.repository;

import com.example.momentory.domain.tag.entity.Tag;
import com.example.momentory.domain.tag.entity.TagType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {
    Optional<Tag> findByNameAndType(String name, TagType type);
    boolean existsByNameAndType(String name, TagType type);
}