package com.example.momentory.domain.character.controller;

import com.example.momentory.domain.character.dto.ItemDto;
import com.example.momentory.domain.character.entity.ItemCategory;
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

    @GetMapping("/items")
    @Operation(summary = "상점 아이템 목록 조회", description = "상점에서 구매 가능한 아이템을 조회합니다.")
    public ApiResponse<List<ItemDto.ShopItemResponse>> getAllShopItems(
            @RequestParam(required = false) ItemCategory category) {
        List<ItemDto.ShopItemResponse> response = shopService.getAllShopItems(category);
        return ApiResponse.onSuccess(response);
    }

    @PostMapping("/buy/{itemId}")
    @Operation(summary = "아이템 구매", description = "상점에서 아이템을 구매합니다.")
    public ApiResponse<ItemDto.Response> buyItem(@PathVariable Long itemId) {
        ItemDto.Response response = itemService.buyItem(itemId);
        return ApiResponse.onSuccess(response);
    }
}
