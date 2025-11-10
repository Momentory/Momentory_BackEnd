package com.example.momentory.domain.character.converter;

import com.example.momentory.domain.character.dto.AdminItemDto;
import com.example.momentory.domain.character.dto.CharacterDto;
import com.example.momentory.domain.character.dto.EventDto;
import com.example.momentory.domain.character.dto.ItemDto;
import com.example.momentory.domain.character.dto.WardrobeDto;
import com.example.momentory.domain.character.entity.Character;
import com.example.momentory.domain.character.entity.CharacterItem;
import com.example.momentory.domain.character.entity.Event;
import com.example.momentory.domain.character.entity.UserItem;
import com.example.momentory.domain.character.entity.Wardrobe;
import com.example.momentory.domain.character.util.LevelCalculator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CharacterConverter {

    private final LevelCalculator levelCalculator;

    public CharacterDto.Response toCharacterResponse(Character character) {
        return CharacterDto.Response.builder()
                .characterId(character.getCharacterId())
                .characterType(character.getCharacterType())
                .level(character.getLevel())
                .isCurrentCharacter(character.isCurrentCharacter())
                .equippedItems(toEquippedItems(character))
                .build();
    }

    public CharacterDto.CurrentCharacterResponse toCurrentCharacterResponse(Character character, int totalPoints) {
        // 레벨 정보 계산
        int currentLevel = character.getLevel();
        int nextLevelPoints = levelCalculator.getRequiredPointsForLevel(currentLevel + 1);
        int remainingPoints = levelCalculator.getPointsUntilNextLevel(totalPoints);
        int pointsForNextLevel = levelCalculator.getPointsForNextLevel(currentLevel);
        
        CharacterDto.LevelInfo levelInfo = CharacterDto.LevelInfo.builder()
                .currentPoints(totalPoints)
                .nextLevelPoints(nextLevelPoints)
                .remainingPoints(remainingPoints)
                .pointsForNextLevel(pointsForNextLevel)
                .build();

        return CharacterDto.CurrentCharacterResponse.builder()
                .characterId(character.getCharacterId())
                .characterType(character.getCharacterType())
                .level(currentLevel)
                .levelInfo(levelInfo)
                .equipped(toEquippedItems(character))
                .build();
    }

    public CharacterDto.ListResponse toCharacterListResponse(Character character) {
        return CharacterDto.ListResponse.builder()
                .characterId(character.getCharacterId())
                .characterType(character.getCharacterType())
                .level(character.getLevel())
                .isCurrentCharacter(character.isCurrentCharacter())
                .equippedItems(toEquippedItems(character))
                .build();
    }

    private CharacterDto.EquippedItems toEquippedItems(Character character) {
        return CharacterDto.EquippedItems.builder()
                .clothing(toItemInfo(character.getEquippedClothing()))
                .expression(toItemInfo(character.getEquippedExpression()))
                .effect(toItemInfo(character.getEquippedEffect()))
                .decoration(toItemInfo(character.getEquippedDecoration()))
                .build();
    }

    private CharacterDto.ItemInfo toItemInfo(UserItem userItem) {
        if (userItem == null) {
            return null;
        }
        return CharacterDto.ItemInfo.builder()
                .itemId(userItem.getItem().getItemId())
                .name(userItem.getItem().getName())
                .imageUrl(userItem.getItem().getImageUrl())
                .build();
    }

    public ItemDto.Response toItemResponse(UserItem userItem) {
        return ItemDto.Response.builder()
                .itemId(userItem.getItem().getItemId())
                .name(userItem.getItem().getName())
                .category(userItem.getItem().getCategory())
                .imageUrl(userItem.getItem().getImageUrl())
                .price(userItem.getItem().getPrice())
                .unlockLevel(userItem.getItem().getUnlockLevel())
                .isOwned(true)
                .isEquipped(userItem.isEquipped())
                .build();
    }

    public ItemDto.ShopItemResponse toShopItemResponse(CharacterItem item, boolean isOwned) {
        return ItemDto.ShopItemResponse.builder()
                .itemId(item.getItemId())
                .name(item.getName())
                .category(item.getCategory())
                .imageUrl(item.getImageUrl())
                .price(item.getPrice())
                .unlockLevel(item.getUnlockLevel())
                .isOwned(isOwned)
                .build();
    }

    public List<ItemDto.ShopItemResponse> toShopItemResponseList(List<CharacterItem> items, boolean isOwned) {
        return items.stream()
                .map(item -> toShopItemResponse(item, isOwned))
                .toList();
    }

    public ItemDto.MyItemResponse toMyItemResponse(UserItem userItem) {
        return ItemDto.MyItemResponse.builder()
                .itemId(userItem.getItem().getItemId())
                .name(userItem.getItem().getName())
                .category(userItem.getItem().getCategory())
                .imageUrl(userItem.getItem().getImageUrl())
                .isEquipped(userItem.isEquipped())
                .build();
    }

    public WardrobeDto.Response toWardrobeResponse(Wardrobe wardrobe) {
        return WardrobeDto.Response.builder()
                .wardrobeId(wardrobe.getWardrobeId())
                .clothing(toWardrobeItemInfo(wardrobe.getClothing()))
                .expression(toWardrobeItemInfo(wardrobe.getExpression()))
                .effect(toWardrobeItemInfo(wardrobe.getEffect()))
                .decoration(toWardrobeItemInfo(wardrobe.getDecoration()))
                .build();
    }

    public WardrobeDto.ListResponse toWardrobeListResponse(Wardrobe wardrobe) {
        return WardrobeDto.ListResponse.builder()
                .wardrobeId(wardrobe.getWardrobeId())
                .clothing(toWardrobeItemInfo(wardrobe.getClothing()))
                .expression(toWardrobeItemInfo(wardrobe.getExpression()))
                .effect(toWardrobeItemInfo(wardrobe.getEffect()))
                .decoration(toWardrobeItemInfo(wardrobe.getDecoration()))
                .build();
    }

    private WardrobeDto.ItemInfo toWardrobeItemInfo(UserItem userItem) {
        if (userItem == null) {
            return null;
        }
        return WardrobeDto.ItemInfo.builder()
                .itemId(userItem.getItem().getItemId())
                .name(userItem.getItem().getName())
                .imageUrl(userItem.getItem().getImageUrl())
                .build();
    }

    // 관리자용 메서드들
    public AdminItemDto.Response toAdminItemResponse(CharacterItem item) {
        return AdminItemDto.Response.builder()
                .itemId(item.getItemId())
                .name(item.getName())
                .category(item.getCategory())
                .imageName(item.getImageName())
                .imageUrl(item.getImageUrl())
                .price(item.getPrice())
                .unlockLevel(item.getUnlockLevel())
                .isLimited(item.isLimited())
                .eventId(item.getEvent() != null ? item.getEvent().getEventId() : null)
                .eventTitle(item.getEvent() != null ? item.getEvent().getTitle() : null)
                .build();
    }

    public AdminItemDto.ListResponse toAdminItemListResponse(CharacterItem item) {
        return AdminItemDto.ListResponse.builder()
                .itemId(item.getItemId())
                .name(item.getName())
                .category(item.getCategory())
                .imageName(item.getImageName())
                .imageUrl(item.getImageUrl())
                .price(item.getPrice())
                .unlockLevel(item.getUnlockLevel())
                .isLimited(item.isLimited())
                .eventId(item.getEvent() != null ? item.getEvent().getEventId() : null)
                .eventTitle(item.getEvent() != null ? item.getEvent().getTitle() : null)
                .build();
    }

    // 이벤트 관련 변환 메서드
    public EventDto.Response toEventResponse(Event event) {
        LocalDateTime now = LocalDateTime.now();
        return EventDto.Response.builder()
                .eventId(event.getEventId())
                .title(event.getTitle())
                .description(event.getDescription())
                .startDate(event.getStartDate())
                .endDate(event.getEndDate())
                .eventType(event.getEventType())
                .isActive(event.isActive())
                .isOngoing(event.isEventPeriod(now))
                .build();
    }

    public EventDto.ListResponse toEventListResponse(Event event) {
        LocalDateTime now = LocalDateTime.now();
        return EventDto.ListResponse.builder()
                .eventId(event.getEventId())
                .title(event.getTitle())
                .description(event.getDescription())
                .startDate(event.getStartDate())
                .endDate(event.getEndDate())
                .eventType(event.getEventType())
                .isActive(event.isActive())
                .isOngoing(event.isEventPeriod(now))
                .build();
    }

    public EventDto.EventItemInfo toEventItemInfo(CharacterItem item) {
        return EventDto.EventItemInfo.builder()
                .itemId(item.getItemId())
                .name(item.getName())
                .imageUrl(item.getImageUrl())
                .price(item.getPrice())
                .build();
    }
}

