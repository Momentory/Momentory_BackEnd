package com.example.momentory.domain.character.service;

import com.example.momentory.domain.character.converter.CharacterConverter;
import com.example.momentory.domain.character.dto.WardrobeDto;
import com.example.momentory.domain.character.entity.Character;
import com.example.momentory.domain.character.entity.UserItem;
import com.example.momentory.domain.character.entity.Wardrobe;
import com.example.momentory.domain.character.repository.CharacterRepository;
import com.example.momentory.domain.character.repository.UserItemRepository;
import com.example.momentory.domain.character.repository.WardrobeRepository;
import com.example.momentory.domain.user.entity.User;
import com.example.momentory.domain.user.service.UserService;
import com.example.momentory.global.exception.GeneralException;
import com.example.momentory.global.code.status.ErrorStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class WardrobeService {

    private final WardrobeRepository wardrobeRepository;
    private final CharacterRepository characterRepository;
    private final UserItemRepository userItemRepository;
    private final CharacterConverter characterConverter;
    private final UserService userService;

    @Transactional
    public WardrobeDto.Response saveCurrentStyle(WardrobeDto.CreateRequest request) {
        User user = userService.getCurrentUser();
        Character currentCharacter = characterRepository.findCurrentCharacterByOwner(user)
                .orElseThrow(() -> new GeneralException(ErrorStatus.CURRENT_CHARACTER_NOT_FOUND));

        Wardrobe wardrobe = Wardrobe.builder()
                .user(user)
                .clothing(currentCharacter.getEquippedClothing())
                .expression(currentCharacter.getEquippedExpression())
                .effect(currentCharacter.getEquippedEffect())
                .decoration(currentCharacter.getEquippedDecoration())
                .build();

        wardrobe.setUser(user);
        Wardrobe savedWardrobe = wardrobeRepository.save(wardrobe);

        return characterConverter.toWardrobeResponse(savedWardrobe);
    }

    public List<WardrobeDto.ListResponse> getMyWardrobes() {
        User user = userService.getCurrentUser();
        List<Wardrobe> wardrobes = wardrobeRepository.findByUser(user);
        return wardrobes.stream()
                .map(characterConverter::toWardrobeListResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public WardrobeDto.Response applyWardrobeStyle(Long wardrobeId) {
        User user = userService.getCurrentUser();
        Wardrobe wardrobe = wardrobeRepository.findById(wardrobeId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.WARDROBE_NOT_FOUND));

        // 소유자 확인
        if (!wardrobe.getUser().equals(user)) {
            throw new GeneralException(ErrorStatus.WARDROBE_ACCESS_DENIED);
        }

        Character currentCharacter = characterRepository.findCurrentCharacterByOwner(user)
                .orElseThrow(() -> new GeneralException(ErrorStatus.CURRENT_CHARACTER_NOT_FOUND));

        // 옷장의 스타일을 현재 캐릭터에 적용 (기존 동일 카테고리 해제 및 플래그 정리)
        if (wardrobe.getClothing() != null) {
            if (currentCharacter.getEquippedClothing() != null) {
                currentCharacter.getEquippedClothing().setEquipped(false);
            }
            currentCharacter.unequipItem(wardrobe.getClothing().getItem().getCategory());
            currentCharacter.equipItem(wardrobe.getClothing());
            wardrobe.getClothing().setEquipped(true);
        }
        if (wardrobe.getExpression() != null) {
            if (currentCharacter.getEquippedExpression() != null) {
                currentCharacter.getEquippedExpression().setEquipped(false);
            }
            currentCharacter.unequipItem(wardrobe.getExpression().getItem().getCategory());
            currentCharacter.equipItem(wardrobe.getExpression());
            wardrobe.getExpression().setEquipped(true);
        }
        if (wardrobe.getEffect() != null) {
            if (currentCharacter.getEquippedEffect() != null) {
                currentCharacter.getEquippedEffect().setEquipped(false);
            }
            currentCharacter.unequipItem(wardrobe.getEffect().getItem().getCategory());
            currentCharacter.equipItem(wardrobe.getEffect());
            wardrobe.getEffect().setEquipped(true);
        }
        if (wardrobe.getDecoration() != null) {
            if (currentCharacter.getEquippedDecoration() != null) {
                currentCharacter.getEquippedDecoration().setEquipped(false);
            }
            currentCharacter.unequipItem(wardrobe.getDecoration().getItem().getCategory());
            currentCharacter.equipItem(wardrobe.getDecoration());
            wardrobe.getDecoration().setEquipped(true);
        }

        return characterConverter.toWardrobeResponse(wardrobe);
    }
}

