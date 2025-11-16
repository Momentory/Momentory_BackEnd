package com.example.momentory.domain.character.controller;

import com.example.momentory.domain.character.converter.CharacterConverter;
import com.example.momentory.domain.character.dto.CharacterDto;
import com.example.momentory.domain.character.service.CharacterService;
import com.example.momentory.global.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/characters")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Character", description = "캐릭터 관련 API")
public class CharacterController {

    private final CharacterService characterService;
    private final CharacterConverter characterConverter;

    @PostMapping
    @Operation(summary = "캐릭터 생성", description = "새로운 캐릭터를 생성합니다.")
    public ApiResponse<CharacterDto.Response> createCharacter(@RequestBody CharacterDto.CreateRequest request) {
        CharacterDto.Response response = characterService.createCharacter(request);
        return ApiResponse.onSuccess(response);
    }

    @PatchMapping("/{characterId}/select")
    @Operation(summary = "캐릭터 선택", description = "현재 캐릭터를 선택합니다.")
    public ApiResponse<CharacterDto.Response> selectCharacter(@PathVariable Long characterId) {
        return ApiResponse.onSuccess(characterService.selectCharacter(characterId));
    }

    @GetMapping("/current")
    @Operation(summary = "현재 캐릭터 조회", description = "현재 선택된 캐릭터와 착용 아이템, 레벨 상세 정보를 조회합니다.")
    public ApiResponse<CharacterDto.CurrentCharacterResponse> getCurrentCharacter() {
        return ApiResponse.onSuccess(characterService.getCurrentCharacterWithLevelInfo());
    }

    @GetMapping
    @Operation(summary = "전체 캐릭터 타입 조회", description = "모든 가능한 캐릭터 타입 목록을 조회합니다.")
    public ApiResponse<List<CharacterDto.CharacterTypeResponse>> getAllCharacterTypes() {
        return ApiResponse.onSuccess(characterService.getAllCharacterTypes());
    }

    @GetMapping("/me")
    @Operation(summary = "사용자 보유 캐릭터 조회", description = "사용자가 보유한 모든 캐릭터 목록을 조회합니다.")
    public ApiResponse<List<CharacterDto.ListResponse>> getMyCharacters() {
        return ApiResponse.onSuccess(characterService.getAllCharacters());
    }

    @PatchMapping("/{characterId}/equip/{itemId}")
    @Operation(summary = "아이템 착용", description = "캐릭터에 아이템을 착용합니다.")
    public ApiResponse<CharacterDto.Response> equipItem(@PathVariable Long characterId, @PathVariable Long itemId) {
        return ApiResponse.onSuccess(characterService.equipItem(characterId, itemId));
    }

    @PatchMapping("/{characterId}/unequip/{itemId}")
    @Operation(summary = "아이템 해제", description = "캐릭터에서 아이템을 해제합니다.")
    public ApiResponse<CharacterDto.Response> unequipItem(@PathVariable Long characterId, @PathVariable Long itemId) {
        return ApiResponse.onSuccess(characterService.unequipItem(characterId, itemId));
    }

}
