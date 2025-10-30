package com.example.momentory.domain.character.controller;

import com.example.momentory.domain.character.dto.AdminItemDto;
import com.example.momentory.domain.character.service.AdminItemService;
import com.example.momentory.global.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/items")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Admin Item", description = "관리자 아이템 관리 API")
public class AdminItemController {

    private final AdminItemService adminItemService;

    @PostMapping
    @Operation(summary = "아이템 생성", description = "새로운 캐릭터 아이템을 생성합니다.")
    public ApiResponse<AdminItemDto.Response> createItem(@RequestBody AdminItemDto.CreateRequest request) {
        AdminItemDto.Response response = adminItemService.createItem(request);
        return ApiResponse.onSuccess(response);
    }

    @GetMapping
    @Operation(summary = "아이템 목록 조회", description = "모든 캐릭터 아이템 목록을 조회합니다.")
    public ApiResponse<List<AdminItemDto.ListResponse>> getAllItems() {
        List<AdminItemDto.ListResponse> response = adminItemService.getAllItems();
        return ApiResponse.onSuccess(response);
    }

    @GetMapping("/{itemId}")
    @Operation(summary = "아이템 상세 조회", description = "특정 아이템의 상세 정보를 조회합니다.")
    public ApiResponse<AdminItemDto.Response> getItem(@PathVariable Long itemId) {
        AdminItemDto.Response response = adminItemService.getItem(itemId);
        return ApiResponse.onSuccess(response);
    }

    @PutMapping("/{itemId}")
    @Operation(summary = "아이템 수정", description = "기존 아이템의 정보를 수정합니다.")
    public ApiResponse<AdminItemDto.Response> updateItem(
            @PathVariable Long itemId,
            @RequestBody AdminItemDto.UpdateRequest request) {
        AdminItemDto.Response response = adminItemService.updateItem(itemId, request);
        return ApiResponse.onSuccess(response);
    }

    @DeleteMapping("/{itemId}")
    @Operation(summary = "아이템 삭제", description = "아이템을 삭제합니다.")
    public ApiResponse<String> deleteItem(@PathVariable Long itemId) {
        adminItemService.deleteItem(itemId);
        return ApiResponse.onSuccess("아이템이 성공적으로 삭제되었습니다.");
    }
}
