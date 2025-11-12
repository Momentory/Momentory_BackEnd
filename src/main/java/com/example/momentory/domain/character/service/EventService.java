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
        Event event = Event.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .eventType(request.getEventType())
                .isActive(request.isActive())
                .build();

        Event savedEvent = eventRepository.save(event);
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

    public List<EventDto.ListResponse> getEventsByType(EventType eventType) {
        List<Event> events = eventRepository.findByEventType(eventType);
        return events.stream()
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

        // 이벤트를 삭제하면 연결된 아이템들의 event 참조가 null이 됨
        // 필요하다면 여기서 연결된 아이템들을 처리할 수 있음
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

    // 이벤트 기간 검증 (다른 서비스에서 사용)
    public void validateEventPeriod(Event event) {
        if (!event.isActive()) {
            throw new GeneralException(ErrorStatus.EVENT_NOT_ACTIVE);
        }

        LocalDateTime now = LocalDateTime.now();
        if (!event.isEventPeriod(now)) {
            throw new GeneralException(ErrorStatus.EVENT_NOT_IN_PERIOD);
        }
    }
}