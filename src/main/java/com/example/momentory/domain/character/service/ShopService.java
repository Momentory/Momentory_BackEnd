package com.example.momentory.domain.character.service;

import com.example.momentory.domain.character.converter.CharacterConverter;
import com.example.momentory.domain.character.dto.ItemDto;
import com.example.momentory.domain.character.entity.Character;
import com.example.momentory.domain.character.entity.CharacterItem;
import com.example.momentory.domain.character.entity.ItemCategory;
import com.example.momentory.domain.character.repository.CharacterItemRepository;
import com.example.momentory.domain.character.repository.UserItemRepository;
import com.example.momentory.domain.user.entity.User;
import com.example.momentory.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ShopService {

    private final CharacterItemRepository characterItemRepository;
    private final UserItemRepository userItemRepository;
    private final CharacterConverter characterConverter;
    private final UserService userService;
    private final CharacterService characterService;

    public List<ItemDto.ShopItemResponse> getAllShopItems(ItemCategory category) {
        User user = userService.getCurrentUser();
        
        // 현재 캐릭터의 레벨 확인
        Character currentCharacter = characterService.getCurrentCharacter();
        int currentLevel = currentCharacter.getLevel();
        
        log.info("사용자 상점 조회 - 현재 레벨: {}, 카테고리: {}", currentLevel, category);
        
        // 모든 아이템 조회 후 필터링
        List<CharacterItem> allItems = characterItemRepository.findAllByOrderByPriceAsc();
        
        return allItems.stream()
                .filter(item -> item.getUnlockLevel() <= currentLevel) // 레벨 제한 필터링
                .filter(item -> category == null || item.getCategory() == category) // 카테고리 필터링
                .map(item -> {
                    boolean isOwned = userItemRepository.existsByUserAndItem_ItemId(user, item.getItemId());
                    return characterConverter.toShopItemResponse(item, isOwned);
                })
                .collect(Collectors.toList());
    }
}

