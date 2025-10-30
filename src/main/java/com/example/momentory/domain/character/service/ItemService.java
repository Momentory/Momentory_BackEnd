package com.example.momentory.domain.character.service;

import com.example.momentory.domain.character.converter.CharacterConverter;
import com.example.momentory.domain.character.dto.ItemDto;
import com.example.momentory.domain.character.entity.CharacterItem;
import com.example.momentory.domain.character.entity.UserItem;
import com.example.momentory.domain.character.repository.CharacterItemRepository;
import com.example.momentory.domain.character.repository.UserItemRepository;
import com.example.momentory.domain.point.entity.PointActionType;
import com.example.momentory.domain.point.entity.PointHistory;
import com.example.momentory.domain.point.repository.PointHistoryRepository;
import com.example.momentory.domain.user.entity.User;
import com.example.momentory.domain.user.entity.UserProfile;
import com.example.momentory.domain.user.repository.UserProfileRepository;
import com.example.momentory.global.exception.GeneralException;
import com.example.momentory.global.code.status.ErrorStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ItemService {

    private final CharacterItemRepository characterItemRepository;
    private final UserItemRepository userItemRepository;
    private final CharacterConverter characterConverter;
    private final UserProfileRepository userProfileRepository;
    private final PointHistoryRepository pointHistoryRepository;

    public List<ItemDto.MyItemResponse> getMyItems(User user) {
        List<UserItem> userItems = userItemRepository.findByUser(user);
        return userItems.stream()
                .map(characterConverter::toMyItemResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public ItemDto.Response buyItem(User user, Long itemId) {
        CharacterItem item = characterItemRepository.findById(itemId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.ITEM_NOT_FOUND));

        // 이미 보유한 아이템인지 확인
        if (userItemRepository.existsByUserAndItem_ItemId(user, itemId)) {
            throw new GeneralException(ErrorStatus.ITEM_ALREADY_OWNED);
        }

        // UserProfile 조회
        UserProfile userProfile = userProfileRepository.findByUser(user)
                .orElseThrow(() -> new GeneralException(ErrorStatus.RESOURCE_NOT_FOUND));

        // 포인트 확인
        if (!userProfile.hasEnoughPoints(item.getPrice())) {
            throw new GeneralException(ErrorStatus.INSUFFICIENT_POINTS);
        }

        // 포인트 차감
        userProfile.minusPoint(item.getPrice());

        // 포인트 히스토리 기록
        PointHistory pointHistory = PointHistory.builder()
                .user(user)
                .actionType(PointActionType.BUY_ITEM)
                .amount(-item.getPrice())  // 음수로 기록 (차감)
                .build();
        pointHistoryRepository.save(pointHistory);

        // UserItem 생성
        UserItem userItem = UserItem.builder()
                .user(user)
                .item(item)
                .isEquipped(false)
                .build();

        UserItem savedUserItem = userItemRepository.save(userItem);

        return characterConverter.toItemResponse(savedUserItem);
    }

    @Transactional
    public ItemDto.Response giveRandomItem(User user) {
        List<CharacterItem> allItems = characterItemRepository.findAll();
        if (allItems.isEmpty()) {
            throw new GeneralException(ErrorStatus.NO_ITEMS_AVAILABLE);
        }

        Random random = new Random();
        CharacterItem randomItem = allItems.get(random.nextInt(allItems.size()));

        // 이미 보유한 아이템인지 확인
        if (userItemRepository.existsByUserAndItem_ItemId(user, randomItem.getItemId())) {
            // 이미 보유한 경우 다른 아이템 선택
            List<CharacterItem> notOwnedItems = allItems.stream()
                    .filter(item -> !userItemRepository.existsByUserAndItem_ItemId(user, item.getItemId()))
                    .collect(Collectors.toList());

            if (notOwnedItems.isEmpty()) {
                throw new GeneralException(ErrorStatus.ALL_ITEMS_OWNED);
            }

            randomItem = notOwnedItems.get(random.nextInt(notOwnedItems.size()));
        }

        // UserItem 생성
        UserItem userItem = UserItem.builder()
                .user(user)
                .item(randomItem)
                .isEquipped(false)
                .build();

        UserItem savedUserItem = userItemRepository.save(userItem);

        return characterConverter.toItemResponse(savedUserItem);
    }
}
