package com.example.momentory.domain.character.service;

import com.example.momentory.domain.character.converter.CharacterConverter;
import com.example.momentory.domain.character.dto.CharacterDto;
import com.example.momentory.domain.character.entity.Character;
import com.example.momentory.domain.character.entity.CharacterType;
import com.example.momentory.domain.character.entity.UserItem;
import com.example.momentory.domain.character.repository.CharacterRepository;
import com.example.momentory.domain.character.repository.UserItemRepository;
import com.example.momentory.domain.character.util.LevelCalculator;
import com.example.momentory.domain.notification.entity.NotificationType;
import com.example.momentory.domain.notification.event.NotificationEvent;
import com.example.momentory.domain.point.entity.PointActionType;
import com.example.momentory.domain.point.entity.PointHistory;
import com.example.momentory.domain.point.repository.PointHistoryRepository;
import com.example.momentory.domain.user.entity.User;
import com.example.momentory.domain.user.service.UserService;
import com.example.momentory.global.exception.GeneralException;
import com.example.momentory.global.code.status.ErrorStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
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
    private final UserService userService;
    private final ApplicationEventPublisher eventPublisher;

    private static final int LEVEL_UP_BONUS = 200;

    @Transactional
    public CharacterDto.Response createCharacter(CharacterDto.CreateRequest request) {
        User user = userService.getCurrentUser();
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

    public void createCharacterSigninUser(User user, CharacterType characterType){
        Character character = Character.builder()
                .characterType(characterType)
                .level(1)
                .isStarter(true)
                .owner(user)
                .isCurrentCharacter(true)
                .build();

        character.setOwner(user);
        characterRepository.save(character);
    }

    @Transactional
    public CharacterDto.Response selectCharacter(Long characterId) {
        User user = userService.getCurrentUser();
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
    public Character getCurrentCharacter() {
        User user = userService.getCurrentUser();
        Character currentCharacter = characterRepository.findCurrentCharacterByOwner(user)
                .orElseThrow(() -> new GeneralException(ErrorStatus.CURRENT_CHARACTER_NOT_FOUND));

        // 레벨 갱신
        updateCharacterLevel(currentCharacter, user);

        return currentCharacter;
    }

    /**
     * 현재 캐릭터 조회 (레벨 상세 정보 포함)
     */
    @Transactional
    public CharacterDto.CurrentCharacterResponse getCurrentCharacterWithLevelInfo() {
        User user = userService.getCurrentUser();
        Character currentCharacter = characterRepository.findCurrentCharacterByOwner(user)
                .orElseThrow(() -> new GeneralException(ErrorStatus.CURRENT_CHARACTER_NOT_FOUND));

        // 레벨 갱신
        updateCharacterLevel(currentCharacter, user);

        // 누적 포인트 계산
        int totalPoints = pointHistoryRepository.calculateTotalPointsByUser(user);

        // Converter를 통해 레벨 상세 정보 포함하여 반환
        return characterConverter.toCurrentCharacterResponse(currentCharacter, totalPoints);
    }

    @Transactional
    public List<CharacterDto.ListResponse> getAllCharacters() {
        User user = userService.getCurrentUser();
        List<Character> characters = characterRepository.findByOwner(user);

        // 모든 캐릭터의 레벨을 갱신
        characters.forEach(character -> updateCharacterLevel(character, user));

        return characters.stream()
                .map(characterConverter::toCharacterListResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public CharacterDto.Response equipItem(Long characterId, Long itemId) {
        User user = userService.getCurrentUser();
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
    public CharacterDto.Response unequipItem(Long characterId, Long itemId) {
        User user = userService.getCurrentUser();
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
     * 레벨이 오를 경우 보너스 포인트를 지급합니다.
     */
    @Transactional
    public void updateCharacterLevel(Character character, User user) {
        int totalPoints = pointHistoryRepository.calculateTotalPointsByUser(user);
        int newLevel = levelCalculator.calculateLevel(totalPoints);

        if (character.getLevel() != newLevel) {
            int oldLevel = character.getLevel();
            character.updateLevel(newLevel);
            characterRepository.save(character);

            user.getProfile().plusPoint(LEVEL_UP_BONUS);

            PointHistory levelUpHistory = PointHistory.builder()
                    .user(user)
                    .amount(LEVEL_UP_BONUS)
                    .actionType(PointActionType.LEVELUP)
                    .build();
            pointHistoryRepository.save(levelUpHistory);

            // 레벨업 알림 발송
            NotificationEvent event = NotificationEvent.builder()
                    .targetUser(user)
                    .type(NotificationType.LEVEL_UP)
                    .message("축하합니다! 레벨 " + newLevel + "로 레벨업했습니다. (+" + LEVEL_UP_BONUS + " 포인트)")
                    .relatedId(character.getCharacterId())
                    .build();
            eventPublisher.publishEvent(event);
        }
    }

}

