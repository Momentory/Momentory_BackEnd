package com.example.momentory.domain.character.service;

import com.example.momentory.domain.character.converter.CharacterConverter;
import com.example.momentory.domain.character.dto.CharacterDto;
import com.example.momentory.domain.character.entity.Character;
import com.example.momentory.domain.character.entity.CharacterType;
import com.example.momentory.domain.character.entity.UserItem;
import com.example.momentory.domain.character.repository.CharacterRepository;
import com.example.momentory.domain.character.repository.UserItemRepository;
import com.example.momentory.domain.character.util.LevelCalculator;
import com.example.momentory.domain.point.repository.PointHistoryRepository;
import com.example.momentory.domain.user.entity.User;
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
public class CharacterService {

    private final CharacterRepository characterRepository;
    private final UserItemRepository userItemRepository;
    private final CharacterConverter characterConverter;
    private final PointHistoryRepository pointHistoryRepository;
    private final LevelCalculator levelCalculator;

    @Transactional
    public CharacterDto.Response createCharacter(User user, CharacterDto.CreateRequest request) {
        // 이미 같은 타입의 캐릭터가 있는지 확인
        if (characterRepository.existsByOwnerAndCharacterType(user, request.getCharacterType())) {
            throw new GeneralException(ErrorStatus.CHARACTER_TYPE_ALREADY_EXISTS);
        }

        // 누적 포인트 계산
        int totalPoints = pointHistoryRepository.calculateTotalPointsByUser(user);
        int level = levelCalculator.calculateLevel(totalPoints);

        Character character = Character.builder()
                .characterType(request.getCharacterType())
                .level(level)
                .isStarter(true)
                .owner(user)
                .isCurrentCharacter(false)
                .build();

        character.setOwner(user);
        Character savedCharacter = characterRepository.save(character);

        return characterConverter.toCharacterResponse(savedCharacter);
    }

    @Transactional
    public CharacterDto.Response selectCharacter(User user, Long characterId) {
        Character character = characterRepository.findById(characterId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.CHARACTER_NOT_FOUND));

        // 소유자 확인
        if (!character.getOwner().equals(user)) {
            throw new GeneralException(ErrorStatus.CHARACTER_ACCESS_DENIED);
        }

        // 기존 현재 캐릭터 해제
        characterRepository.findCurrentCharacterByOwner(user)
                .ifPresent(currentChar -> currentChar.unsetAsCurrentCharacter());

        // 새로운 캐릭터를 현재 캐릭터로 설정
        character.setAsCurrentCharacter();

        // 레벨 갱신
        updateCharacterLevel(character, user);

        return characterConverter.toCharacterResponse(character);
    }

    @Transactional
    public CharacterDto.CurrentCharacterResponse getCurrentCharacter(User user) {
        Character currentCharacter = characterRepository.findCurrentCharacterByOwner(user)
                .orElseThrow(() -> new GeneralException(ErrorStatus.CURRENT_CHARACTER_NOT_FOUND));

        // 레벨 갱신
        updateCharacterLevel(currentCharacter, user);

        return characterConverter.toCurrentCharacterResponse(currentCharacter);
    }

    @Transactional
    public List<CharacterDto.ListResponse> getAllCharacters(User user) {
        List<Character> characters = characterRepository.findByOwner(user);
        
        // 모든 캐릭터의 레벨을 갱신
        characters.forEach(character -> updateCharacterLevel(character, user));
        
        return characters.stream()
                .map(characterConverter::toCharacterListResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public CharacterDto.Response equipItem(User user, Long characterId, Long itemId) {
        Character character = characterRepository.findById(characterId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.CHARACTER_NOT_FOUND));

        // 소유자 확인
        if (!character.getOwner().equals(user)) {
            throw new GeneralException(ErrorStatus.CHARACTER_ACCESS_DENIED);
        }

        UserItem userItem = userItemRepository.findByUserAndItem_ItemId(user, itemId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.ITEM_NOT_OWNED));

        // 같은 카테고리의 기존 아이템 해제 및 플래그 정리
        switch (userItem.getItem().getCategory()) {
            case CLOTHING -> {
                if (character.getEquippedClothing() != null) {
                    character.getEquippedClothing().setEquipped(false);
                }
            }
            case EXPRESSION -> {
                if (character.getEquippedExpression() != null) {
                    character.getEquippedExpression().setEquipped(false);
                }
            }
            case EFFECT -> {
                if (character.getEquippedEffect() != null) {
                    character.getEquippedEffect().setEquipped(false);
                }
            }
            case DECORATION -> {
                if (character.getEquippedDecoration() != null) {
                    character.getEquippedDecoration().setEquipped(false);
                }
            }
        }

        character.unequipItem(userItem.getItem().getCategory());

        // 새 아이템 착용 및 플래그 설정
        character.equipItem(userItem);
        userItem.setEquipped(true);

        // 레벨 갱신
        updateCharacterLevel(character, user);

        return characterConverter.toCharacterResponse(character);
    }

    @Transactional
    public CharacterDto.Response unequipItem(User user, Long characterId, Long itemId) {
        Character character = characterRepository.findById(characterId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.CHARACTER_NOT_FOUND));

        // 소유자 확인
        if (!character.getOwner().equals(user)) {
            throw new GeneralException(ErrorStatus.CHARACTER_ACCESS_DENIED);
        }

        UserItem userItem = userItemRepository.findByUserAndItem_ItemId(user, itemId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.ITEM_NOT_OWNED));

        // 아이템 해제
        character.unequipItem(userItem.getItem().getCategory());
        userItem.setEquipped(false);

        // 레벨 갱신
        updateCharacterLevel(character, user);

        return characterConverter.toCharacterResponse(character);
    }

    /**
     * 캐릭터의 레벨을 사용자의 누적 포인트 기준으로 갱신합니다.
     */
    private void updateCharacterLevel(Character character, User user) {
        int totalPoints = pointHistoryRepository.calculateTotalPointsByUser(user);
        int newLevel = levelCalculator.calculateLevel(totalPoints);
        
        if (character.getLevel() != newLevel) {
            character.updateLevel(newLevel);
            characterRepository.save(character);
        }
    }
}

