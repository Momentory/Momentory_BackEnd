package com.example.momentory.domain.character.service;

import com.example.momentory.domain.character.converter.CharacterConverter;
import com.example.momentory.domain.character.dto.ItemDto;
import com.example.momentory.domain.character.entity.CharacterItem;
import com.example.momentory.domain.character.repository.CharacterItemRepository;
import com.example.momentory.domain.character.repository.UserItemRepository;
import com.example.momentory.domain.user.entity.User;
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

    public List<ItemDto.ShopItemResponse> getAllShopItems(User user) {
        List<CharacterItem> allItems = characterItemRepository.findAllByOrderByPriceAsc();
        
        return allItems.stream()
                .map(item -> {
                    boolean isOwned = userItemRepository.existsByUserAndItem_ItemId(user, item.getItemId());
                    return characterConverter.toShopItemResponse(item, isOwned);
                })
                .collect(Collectors.toList());
    }
}

