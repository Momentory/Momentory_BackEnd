package com.example.momentory.domain.character.controller;

import com.example.momentory.domain.character.dto.EventDto;
import com.example.momentory.domain.character.entity.status.EventType;
import com.example.momentory.domain.character.service.EventService;
import com.example.momentory.global.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Event", description = "사용자 이벤트 조회 API")
public class EventController {

    private final EventService eventService;

    @GetMapping("/active")
    @Operation(summary = "진행 중인 이벤트 조회", description = "현재 진행 중인 이벤트 목록을 조회합니다.")
    public ApiResponse<List<EventDto.ListResponse>> getActiveEvents() {
        List<EventDto.ListResponse> response = eventService.getActiveEvents();
        return ApiResponse.onSuccess(response);
    }

    @GetMapping("/upcoming")
    @Operation(summary = "예정된 이벤트 조회", description = "앞으로 시작될 이벤트 목록을 조회합니다.")
    public ApiResponse<List<EventDto.ListResponse>> getUpcomingEvents() {
        List<EventDto.ListResponse> response = eventService.getUpcomingEvents();
        return ApiResponse.onSuccess(response);
    }

    @GetMapping("/{eventId}")
    @Operation(summary = "이벤트 상세 조회", description = "특정 이벤트의 상세 정보를 조회합니다.")
    public ApiResponse<EventDto.Response> getEvent(@PathVariable Long eventId) {
        EventDto.Response response = eventService.getEvent(eventId);
        return ApiResponse.onSuccess(response);
    }

    @GetMapping("/{eventId}/items")
    @Operation(summary = "이벤트 아이템 조회", description = "특정 이벤트에 연결된 아이템 목록을 조회합니다.")
    public ApiResponse<EventDto.EventWithItemsResponse> getEventWithItems(@PathVariable Long eventId) {
        EventDto.EventWithItemsResponse response = eventService.getEventWithItems(eventId);
        return ApiResponse.onSuccess(response);
    }
}