package com.example.momentory.domain.character.service;

import com.example.momentory.domain.character.converter.CharacterConverter;
import com.example.momentory.domain.character.dto.ItemDto;
import com.example.momentory.domain.character.entity.Character;
import com.example.momentory.domain.character.entity.CharacterItem;
import com.example.momentory.domain.character.entity.status.ItemCategory;
import com.example.momentory.domain.character.entity.UserItem;
import com.example.momentory.domain.character.repository.CharacterItemRepository;
import com.example.momentory.domain.character.repository.UserItemRepository;
import com.example.momentory.domain.point.entity.PointActionType;
import com.example.momentory.domain.point.service.PointService;
import com.example.momentory.domain.user.entity.User;
import com.example.momentory.domain.user.repository.UserProfileRepository;
import com.example.momentory.domain.user.service.UserService;
import com.example.momentory.global.exception.GeneralException;
import com.example.momentory.global.code.status.ErrorStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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
    private final CharacterService characterService;

    public List<ItemDto.MyItemResponse> getMyItems(ItemCategory category) {
        User user = userService.getCurrentUser();
        List<UserItem> userItems = userItemRepository.findByUser(user);
        
        return userItems.stream()
                .filter(userItem -> category == null || userItem.getItem().getCategory() == category) // 카테고리 필터링
                .map(characterConverter::toMyItemResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public ItemDto.Response buyItem(Long itemId) {
        User user = userService.getCurrentUser();

        // 현재 캐릭터의 레벨 확인
        Character currentCharacter = characterService.getCurrentCharacter();
        int currentLevel = currentCharacter.getLevel();

        CharacterItem item = characterItemRepository.findById(itemId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.ITEM_NOT_FOUND));

        // 레벨 제한 확인
        if (item.getUnlockLevel() > currentLevel) {
            log.warn("아이템 구매 실패 - 레벨 부족. 필요 레벨: {}, 현재 레벨: {}", item.getUnlockLevel(), currentLevel);
            throw new GeneralException(ErrorStatus.ITEM_LEVEL_LOCKED);
        }

        // 이벤트 한정 아이템 기간 검증
        if (item.isLimited()) {
            validateEventPeriodForPurchase(item);
        }

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

    /**
     * 이벤트 한정 아이템 구매 시 이벤트 기간 검증
     */
    private void validateEventPeriodForPurchase(CharacterItem item) {
        // 이벤트가 연결되어 있지 않은 경우
        if (item.getEvent() == null) {
            throw new GeneralException(ErrorStatus.EVENT_ITEM_PURCHASE_UNAVAILABLE);
        }

        LocalDateTime now = LocalDateTime.now();

        // 이벤트가 활성화되어 있지 않은 경우
        if (!item.getEvent().isActive()) {
            throw new GeneralException(ErrorStatus.EVENT_NOT_ACTIVE);
        }

        // 이벤트 기간이 아닌 경우
        if (!item.getEvent().isEventPeriod(now)) {
            throw new GeneralException(ErrorStatus.EVENT_ITEM_PURCHASE_UNAVAILABLE);
        }
    }

    @Transactional
    public ItemDto.Response giveRandomItem() {
        User user = userService.getCurrentUser();
        
        // 랜덤 아이템은 레벨 제한 없이 모든 아이템 중에서 지급
        List<CharacterItem> allItems = characterItemRepository.findAll();
        
        if (allItems.isEmpty()) {
            throw new GeneralException(ErrorStatus.NO_ITEMS_AVAILABLE);
        }

        // 보유하지 않은 아이템만 필터링
        List<CharacterItem> notOwnedItems = allItems.stream()
                .filter(item -> !userItemRepository.existsByUserAndItem_ItemId(user, item.getItemId()))
                .collect(Collectors.toList());

        if (notOwnedItems.isEmpty()) {
            throw new GeneralException(ErrorStatus.ALL_ITEMS_OWNED);
        }

        // 랜덤 선택
        Random random = new Random();
        CharacterItem randomItem = notOwnedItems.get(random.nextInt(notOwnedItems.size()));

        // UserItem 생성
        UserItem userItem = UserItem.builder()
                .user(user)
                .item(randomItem)
                .isEquipped(false)
                .build();

        UserItem savedUserItem = userItemRepository.save(userItem);

        return characterConverter.toItemResponse(savedUserItem);
    }

    public List<ItemDto.ShopItemResponse> recentItems(){
        List<CharacterItem> recentItems = characterItemRepository.findTop3ByOrderByCreatedAtDesc();
        return characterConverter.toShopItemResponseList(recentItems, false);
    }
}
