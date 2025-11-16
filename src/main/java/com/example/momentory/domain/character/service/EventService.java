package com.example.momentory.domain.character.service;

import com.example.momentory.domain.character.converter.CharacterConverter;
import com.example.momentory.domain.character.dto.EventDto;
import com.example.momentory.domain.character.entity.CharacterItem;
import com.example.momentory.domain.character.entity.Event;
import com.example.momentory.domain.character.entity.status.EventType;
import com.example.momentory.domain.character.repository.CharacterItemRepository;
import com.example.momentory.domain.character.repository.EventRepository;
import com.example.momentory.global.code.status.ErrorStatus;
import com.example.momentory.global.exception.GeneralException;
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
public class EventService {

    private final EventRepository eventRepository;
    private final CharacterItemRepository characterItemRepository;
    private final CharacterConverter characterConverter;

    @Transactional
    public EventDto.Response createEvent(EventDto.CreateRequest request) {
        // 1. 이벤트 생성
        Event event = Event.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .eventType(request.getEventType())
                .isActive(request.isActive())
                .build();

        Event savedEvent = eventRepository.save(event);

        // 2. 이벤트와 함께 생성할 아이템이 있다면 일괄 생성
        if (request.getItems() != null && !request.getItems().isEmpty()) {
            List<CharacterItem> items = request.getItems().stream()
                    .map(itemRequest -> CharacterItem.builder()
                            .name(itemRequest.getName())
                            .category(itemRequest.getCategory())
                            .imageName(itemRequest.getImageName())
                            .imageUrl(itemRequest.getImageUrl())
                            .price(itemRequest.getPrice())
                            .unlockLevel(itemRequest.getUnlockLevel())
                            .isLimited(itemRequest.isLimited())
                            .event(savedEvent) // 생성된 이벤트와 연결
                            .build())
                    .collect(Collectors.toList());

            characterItemRepository.saveAll(items);
            log.info("이벤트 생성과 함께 {} 개의 아이템이 등록되었습니다.", items.size());
        }

        return characterConverter.toEventResponse(savedEvent);
    }

    public EventDto.Response getEvent(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.EVENT_NOT_FOUND));
        return characterConverter.toEventResponse(event);
    }

    public List<EventDto.ListResponse> getAllEvents() {
        List<Event> events = eventRepository.findAll();
        return events.stream()
                .map(characterConverter::toEventListResponse)
                .collect(Collectors.toList());
    }

    public List<EventDto.ListResponse> getActiveEvents() {
        LocalDateTime now = LocalDateTime.now();
        List<Event> activeEvents = eventRepository.findActiveEventsInPeriod(now);
        return activeEvents.stream()
                .map(characterConverter::toEventListResponse)
                .collect(Collectors.toList());
    }

    public List<EventDto.ListResponse> getUpcomingEvents() {
        LocalDateTime now = LocalDateTime.now();
        List<Event> upcomingEvents = eventRepository.findUpcomingEvents(now);
        return upcomingEvents.stream()
                .map(characterConverter::toEventListResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public EventDto.Response updateEvent(Long eventId, EventDto.UpdateRequest request) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.EVENT_NOT_FOUND));

        event.update(
                request.getTitle(),
                request.getDescription(),
                request.getStartDate(),
                request.getEndDate(),
                request.getEventType(),
                request.isActive()
        );

        return characterConverter.toEventResponse(event);
    }

    @Transactional
    public void deleteEvent(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.EVENT_NOT_FOUND));

        // 외래키 제약 조건을 위해 연관된 아이템을 먼저 삭제
        characterItemRepository.deleteAllByEvent(event);

        // 이벤트 삭제
        eventRepository.delete(event);
    }

    public EventDto.EventWithItemsResponse getEventWithItems(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.EVENT_NOT_FOUND));

        // 해당 이벤트에 연결된 아이템들 조회
        List<CharacterItem> eventItems = characterItemRepository.findAll().stream()
                .filter(item -> item.getEvent() != null && item.getEvent().getEventId().equals(eventId))
                .collect(Collectors.toList());

        List<EventDto.EventItemInfo> itemInfos = eventItems.stream()
                .map(characterConverter::toEventItemInfo)
                .collect(Collectors.toList());

        LocalDateTime now = LocalDateTime.now();
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
    }

}