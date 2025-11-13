package com.example.momentory.domain.character.service;

import com.example.momentory.domain.character.converter.CharacterConverter;
import com.example.momentory.domain.character.dto.EventDto;
import com.example.momentory.domain.character.dto.ItemDto;
import com.example.momentory.domain.character.entity.Character;
import com.example.momentory.domain.character.entity.CharacterItem;
import com.example.momentory.domain.character.entity.Event;
import com.example.momentory.domain.character.entity.status.ItemCategory;
import com.example.momentory.domain.character.repository.CharacterItemRepository;
import com.example.momentory.domain.character.repository.EventRepository;
import com.example.momentory.domain.character.repository.UserItemRepository;
import com.example.momentory.domain.user.entity.User;
import com.example.momentory.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ShopService {

    private final CharacterItemRepository characterItemRepository;
    private final UserItemRepository userItemRepository;
    private final EventRepository eventRepository;
    private final CharacterConverter characterConverter;
    private final UserService userService;
    private final CharacterService characterService;

    public List<ItemDto.ShopItemResponse> getAllShopItems(ItemCategory category) {
        User user = userService.getCurrentUser();
        LocalDateTime now = LocalDateTime.now();

        // 모든 아이템을 unlockLevel 오름차순으로 조회
        List<CharacterItem> allItems = characterItemRepository.findAllByOrderByUnlockLevelAsc();

        return allItems.stream()
                .filter(item -> category == null || item.getCategory() == category) // 카테고리 필터링
                .filter(item -> isItemAvailable(item, now)) // 이벤트 아이템 기간 검증
                .map(item -> {
                    boolean isOwned = userItemRepository.existsByUserAndItem_ItemId(user, item.getItemId());
                    return characterConverter.toShopItemResponse(item, isOwned);
                })
                .collect(Collectors.toList());
    }

    public List<EventDto.EventWithItemsResponse> getActiveEventsWithItems() {
        LocalDateTime now = LocalDateTime.now();

        // 현재 진행 중인 이벤트 조회
        List<Event> activeEvents = eventRepository.findActiveEventsInPeriod(now);

        // 각 이벤트에 연결된 아이템 조회
        return activeEvents.stream()
                .map(event -> {
                    // 해당 이벤트에 연결된 아이템들 조회
                    List<CharacterItem> eventItems = characterItemRepository.findAll().stream()
                            .filter(item -> item.getEvent() != null && item.getEvent().getEventId().equals(event.getEventId()))
                            .collect(Collectors.toList());

                    List<EventDto.EventItemInfo> itemInfos = eventItems.stream()
                            .map(characterConverter::toEventItemInfo)
                            .collect(Collectors.toList());

                    return EventDto.EventWithItemsResponse.builder()
                            .eventId(event.getEventId())
                            .title(event.getTitle())
                            .description(event.getDescription())
                            .startDate(event.getStartDate())
                            .endDate(event.getEndDate())
                            .eventType(event.getEventType())
                            .isActive(event.isActive())
                            .isOngoing(event.isEventPeriod(now))
                            .items(itemInfos)
                            .build();
                })
                .collect(Collectors.toList());
    }

    /**
     * 아이템이 현재 구매 가능한지 확인
     * - 일반 아이템: 항상 구매 가능
     * - 이벤트 한정 아이템: 이벤트 기간 내에만 구매 가능
     */
    private boolean isItemAvailable(CharacterItem item, LocalDateTime now) {
        // 이벤트 한정 아이템이 아닌 경우 항상 구매 가능
        if (!item.isLimited()) {
            return true;
        }

        // 이벤트 한정 아이템이지만 이벤트가 연결되어 있지 않은 경우 표시하지 않음
        if (item.getEvent() == null) {
            return false;
        }

        // 이벤트가 활성화되어 있고, 현재 이벤트 기간 내인지 확인
        return item.getEvent().isEventPeriod(now);
    }
}

