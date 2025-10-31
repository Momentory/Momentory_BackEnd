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
import com.example.momentory.domain.point.service.PointService;
import com.example.momentory.domain.user.entity.User;
import com.example.momentory.domain.user.entity.UserProfile;
import com.example.momentory.domain.user.repository.UserProfileRepository;
import com.example.momentory.domain.user.service.UserService;
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
    private final PointService pointService;
    private final UserService userService;

    public List<ItemDto.MyItemResponse> getMyItems() {
        User user = userService.getCurrentUser();
        List<UserItem> userItems = userItemRepository.findByUser(user);
        return userItems.stream()
                .map(characterConverter::toMyItemResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public ItemDto.Response buyItem(Long itemId) {
        User user = userService.getCurrentUser();
        CharacterItem item = characterItemRepository.findById(itemId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.ITEM_NOT_FOUND));

        // 이미 보유한 아이템인지 확인
        if (userItemRepository.existsByUserAndItem_ItemId(user, itemId)) {
            throw new GeneralException(ErrorStatus.ITEM_ALREADY_OWNED);
        }

        // 포인트 차감 + 히스토리 기록을 PointService에서 처리
        pointService.subtractPoint(user, item.getPrice(), PointActionType.BUY_ITEM);

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
    public ItemDto.Response giveRandomItem() {
        User user = userService.getCurrentUser();
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
