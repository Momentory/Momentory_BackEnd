package com.example.momentory.domain.character.service;

import com.example.momentory.domain.character.converter.CharacterConverter;
import com.example.momentory.domain.character.dto.AdminItemDto;
import com.example.momentory.domain.character.entity.CharacterItem;
import com.example.momentory.domain.character.repository.CharacterItemRepository;
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
public class AdminItemService {

    private final CharacterItemRepository characterItemRepository;
    private final CharacterConverter characterConverter;

    @Transactional
    public AdminItemDto.Response createItem(AdminItemDto.CreateRequest request) {
        CharacterItem item = CharacterItem.builder()
                .name(request.getName())
                .category(request.getCategory())
                .imageUrl(request.getImageUrl())
                .price(request.getPrice())
                .unlockLevel(request.getUnlockLevel())
                .build();

        CharacterItem savedItem = characterItemRepository.save(item);
        return characterConverter.toAdminItemResponse(savedItem);
    }

    public List<AdminItemDto.ListResponse> getAllItems() {
        List<CharacterItem> items = characterItemRepository.findAllByOrderByPriceAsc();
        return items.stream()
                .map(characterConverter::toAdminItemListResponse)
                .collect(Collectors.toList());
    }

    public AdminItemDto.Response getItem(Long itemId) {
        CharacterItem item = characterItemRepository.findById(itemId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.ITEM_NOT_FOUND));
        return characterConverter.toAdminItemResponse(item);
    }

    @Transactional
    public AdminItemDto.Response updateItem(Long itemId, AdminItemDto.UpdateRequest request) {
        CharacterItem item = characterItemRepository.findById(itemId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.ITEM_NOT_FOUND));

        // 아이템 정보 업데이트 (감사 시간 필드는 JPA Auditing으로 자동 처리)
        item.update(
                request.getName(),
                request.getCategory(),
                request.getImageUrl(),
                request.getPrice(),
                request.getUnlockLevel()
        );

        return characterConverter.toAdminItemResponse(item);
    }

    @Transactional
    public void deleteItem(Long itemId) {
        CharacterItem item = characterItemRepository.findById(itemId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.ITEM_NOT_FOUND));
        
        characterItemRepository.delete(item);
    }
}
