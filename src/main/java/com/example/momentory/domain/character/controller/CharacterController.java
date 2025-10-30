package com.example.momentory.domain.character.controller;

import com.example.momentory.domain.character.dto.CharacterDto;
import com.example.momentory.domain.character.service.CharacterService;
import com.example.momentory.domain.user.entity.User;
import com.example.momentory.domain.user.repository.UserRepository;
import com.example.momentory.global.ApiResponse;
import com.example.momentory.global.security.SecurityUtils;
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
    private final UserRepository userRepository;

    @PostMapping
    @Operation(summary = "캐릭터 생성", description = "새로운 캐릭터를 생성합니다.")
    public ApiResponse<CharacterDto.Response> createCharacter(@RequestBody CharacterDto.CreateRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        User user = userRepository.findById(userId).orElseThrow();
        CharacterDto.Response response = characterService.createCharacter(user, request);
        return ApiResponse.onSuccess(response);
    }

    @PatchMapping("/{characterId}/select")
    @Operation(summary = "캐릭터 선택", description = "현재 캐릭터를 선택합니다.")
    public ApiResponse<CharacterDto.Response> selectCharacter(@PathVariable Long characterId) {
        Long userId = SecurityUtils.getCurrentUserId();
        User user = userRepository.findById(userId).orElseThrow();
        CharacterDto.Response response = characterService.selectCharacter(user, characterId);
        return ApiResponse.onSuccess(response);
    }

    @GetMapping("/current")
    @Operation(summary = "현재 캐릭터 조회", description = "현재 선택된 캐릭터와 착용 아이템을 조회합니다.")
    public ApiResponse<CharacterDto.CurrentCharacterResponse> getCurrentCharacter() {
        Long userId = SecurityUtils.getCurrentUserId();
        User user = userRepository.findById(userId).orElseThrow();
        CharacterDto.CurrentCharacterResponse response = characterService.getCurrentCharacter(user);
        return ApiResponse.onSuccess(response);
    }

    @GetMapping
    @Operation(summary = "전체 캐릭터 목록 조회", description = "사용자가 보유한 모든 캐릭터 목록을 조회합니다.")
    public ApiResponse<List<CharacterDto.ListResponse>> getAllCharacters() {
        Long userId = SecurityUtils.getCurrentUserId();
        User user = userRepository.findById(userId).orElseThrow();
        List<CharacterDto.ListResponse> response = characterService.getAllCharacters(user);
        return ApiResponse.onSuccess(response);
    }

    @PatchMapping("/{characterId}/equip/{itemId}")
    @Operation(summary = "아이템 착용", description = "캐릭터에 아이템을 착용합니다.")
    public ApiResponse<CharacterDto.Response> equipItem(@PathVariable Long characterId, @PathVariable Long itemId) {
        Long userId = SecurityUtils.getCurrentUserId();
        User user = userRepository.findById(userId).orElseThrow();
        CharacterDto.Response response = characterService.equipItem(user, characterId, itemId);
        return ApiResponse.onSuccess(response);
    }

    @PatchMapping("/{characterId}/unequip/{itemId}")
    @Operation(summary = "아이템 해제", description = "캐릭터에서 아이템을 해제합니다.")
    public ApiResponse<CharacterDto.Response> unequipItem(@PathVariable Long characterId, @PathVariable Long itemId) {
        Long userId = SecurityUtils.getCurrentUserId();
        User user = userRepository.findById(userId).orElseThrow();
        CharacterDto.Response response = characterService.unequipItem(user, characterId, itemId);
        return ApiResponse.onSuccess(response);
    }
}
