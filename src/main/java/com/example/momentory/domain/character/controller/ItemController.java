package com.example.momentory.domain.character.controller;

import com.example.momentory.domain.character.dto.ItemDto;
import com.example.momentory.domain.character.entity.ItemCategory;
import com.example.momentory.domain.character.service.ItemService;
import com.example.momentory.global.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Item", description = "아이템 관련 API")
public class ItemController {

    private final ItemService itemService;

    @GetMapping("/mine")
    @Operation(summary = "내 아이템 목록 조회", description = "사용자가 보유한 아이템을 조회합니다.")
    public ApiResponse<List<ItemDto.MyItemResponse>> getMyItems(@RequestParam ItemCategory category) {
        List<ItemDto.MyItemResponse> response = itemService.getMyItems(category);
        return ApiResponse.onSuccess(response);
    }

    @PostMapping("/random-reward")
    @Operation(summary = "랜덤 아이템 지급", description = "사진 업로드 등으로 랜덤 아이템을 지급받습니다.")
    public ApiResponse<ItemDto.Response> giveRandomItem() {
        ItemDto.Response response = itemService.giveRandomItem();
        return ApiResponse.onSuccess(response);
    }
}
