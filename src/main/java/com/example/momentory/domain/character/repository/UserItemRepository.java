package com.example.momentory.domain.character.repository;

import com.example.momentory.domain.character.entity.UserItem;
import com.example.momentory.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserItemRepository extends JpaRepository<UserItem, Long> {

    List<UserItem> findByUser(User user);

    Optional<UserItem> findByUserAndItem_ItemId(User user, Long itemId);

    @Query("SELECT ui FROM UserItem ui WHERE ui.user = :user AND ui.isEquipped = true")
    List<UserItem> findEquippedItemsByUser(@Param("user") User user);

    boolean existsByUserAndItem_ItemId(User user, Long itemId);
}

