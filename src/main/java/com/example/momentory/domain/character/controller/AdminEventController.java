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
@RequestMapping("/api/admin/events")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Admin Event", description = "관리자 이벤트 관리 API")
public class AdminEventController {

    private final EventService eventService;

    @PostMapping
    @Operation(summary = "이벤트 생성", description = "새로운 이벤트를 생성합니다.")
    public ApiResponse<EventDto.Response> createEvent(@RequestBody EventDto.CreateRequest request) {
        EventDto.Response response = eventService.createEvent(request);
        return ApiResponse.onSuccess(response);
    }

    @GetMapping
    @Operation(summary = "이벤트 목록 조회", description = "모든 이벤트 목록을 조회합니다.")
    public ApiResponse<List<EventDto.ListResponse>> getAllEvents() {
        List<EventDto.ListResponse> response = eventService.getAllEvents();
        return ApiResponse.onSuccess(response);
    }

    @GetMapping("/{eventId}/items")
    @Operation(summary = "이벤트 아이템 조회", description = "특정 이벤트에 연결된 아이템 목록을 조회합니다.")
    public ApiResponse<EventDto.EventWithItemsResponse> getEventWithItems(@PathVariable Long eventId) {
        EventDto.EventWithItemsResponse response = eventService.getEventWithItems(eventId);
        return ApiResponse.onSuccess(response);
    }

    @GetMapping("/active")
    @Operation(summary = "활성 이벤트 조회", description = "현재 진행 중인 이벤트 목록을 조회합니다.")
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

    @PutMapping("/{eventId}")
    @Operation(summary = "이벤트 수정", description = "기존 이벤트의 정보를 수정합니다.")
    public ApiResponse<EventDto.Response> updateEvent(
            @PathVariable Long eventId,
            @RequestBody EventDto.UpdateRequest request) {
        EventDto.Response response = eventService.updateEvent(eventId, request);
        return ApiResponse.onSuccess(response);
    }

    @DeleteMapping("/{eventId}")
    @Operation(summary = "이벤트 삭제", description = "이벤트를 삭제합니다.")
    public ApiResponse<String> deleteEvent(@PathVariable Long eventId) {
        eventService.deleteEvent(eventId);
        return ApiResponse.onSuccess("이벤트가 성공적으로 삭제되었습니다.");
    }
}