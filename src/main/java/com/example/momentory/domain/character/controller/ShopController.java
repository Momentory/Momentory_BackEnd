package com.example.momentory.domain.character.controller;

import com.example.momentory.domain.character.dto.EventDto;
import com.example.momentory.domain.character.dto.ItemDto;
import com.example.momentory.domain.character.entity.status.ItemCategory;
import com.example.momentory.domain.character.service.EventService;
import com.example.momentory.domain.character.service.ItemService;
import com.example.momentory.domain.character.service.ShopService;
import com.example.momentory.global.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shop")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Shop", description = "상점 관련 API")
public class ShopController {

    private final ShopService shopService;
    private final ItemService itemService;
    private final EventService eventService;

    @GetMapping("/items")
    @Operation(summary = "상점 아이템 목록 조회", description = "상점에서 구매 가능한 아이템을 조회합니다.")
    public ApiResponse<List<ItemDto.ShopItemResponse>> getAllShopItems(
            @RequestParam(required = false) ItemCategory category) {
        List<ItemDto.ShopItemResponse> response = shopService.getAllShopItems(category);
        return ApiResponse.onSuccess(response);
    }

    @GetMapping("/events")
    @Operation(summary = "진행 중인 이벤트 목록 조회", description = "상점에서 현재 진행 중인 이벤트와 해당 이벤트의 아이템 목록을 조회합니다.")
    public ApiResponse<List<EventDto.EventWithItemsResponse>> getActiveEventsWithItems() {
        List<EventDto.EventWithItemsResponse> response = shopService.getActiveEventsWithItems();
        return ApiResponse.onSuccess(response);
    }

    @PostMapping("/buy/{itemId}")
    @Operation(summary = "아이템 구매", description = "상점에서 아이템을 구매합니다.")
    public ApiResponse<ItemDto.Response> buyItem(@PathVariable Long itemId) {
        ItemDto.Response response = itemService.buyItem(itemId);
        return ApiResponse.onSuccess(response);
    }

    @GetMapping("/recent-items")
    @Operation(summary = "최근 추가된 아이템 목록 조회", description = "새롭게 추가된 아이템 목록 3가지를 조회합니다.")
    public ApiResponse<List<ItemDto.ShopItemResponse>> getRecentAddItems() {
        return ApiResponse.onSuccess(itemService.recentItems());
    }
}
