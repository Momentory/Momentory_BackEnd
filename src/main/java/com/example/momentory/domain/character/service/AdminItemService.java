package com.example.momentory.domain.character.service;

import com.example.momentory.domain.character.converter.CharacterConverter;
import com.example.momentory.domain.character.dto.AdminItemDto;
import com.example.momentory.domain.character.entity.CharacterItem;
import com.example.momentory.domain.character.entity.Event;
import com.example.momentory.domain.character.repository.CharacterItemRepository;
import com.example.momentory.domain.character.repository.EventRepository;
import com.example.momentory.domain.file.service.S3Service;
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
    private final EventRepository eventRepository;
    private final CharacterConverter characterConverter;
    private final S3Service s3Service;

    @Transactional
    public AdminItemDto.Response createItem(AdminItemDto.CreateRequest request) {
        Event event = null;

        // 이벤트 ID가 제공된 경우 이벤트 조회 및 검증
        if (request.getEventId() != null) {
            event = eventRepository.findById(request.getEventId())
                    .orElseThrow(() -> new GeneralException(ErrorStatus.EVENT_NOT_FOUND));
        }

        CharacterItem item = CharacterItem.builder()
                .name(request.getName())
                .category(request.getCategory())
                .imageName(request.getImageName())
                .imageUrl(request.getImageUrl())
                .price(request.getPrice())
                .unlockLevel(request.getUnlockLevel())
                .isLimited(request.isLimited())
                .event(event)
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

        // 이미지가 변경되는 경우 기존 S3 파일 삭제
        if (request.getImageName() != null && !request.getImageName().equals(item.getImageName())) {
            if (item.getImageName() != null && !item.getImageName().isEmpty()) {
                try {
                    s3Service.deleteFile(item.getImageName());
                } catch (Exception e) {
                    log.error("기존 이미지 S3 삭제 실패 - imageName: {}, 오류: {}", item.getImageName(), e.getMessage());
                }
            }
        }

        // 이벤트 업데이트
        Event event = null;
        if (request.getEventId() != null) {
            event = eventRepository.findById(request.getEventId())
                    .orElseThrow(() -> new GeneralException(ErrorStatus.EVENT_NOT_FOUND));
        }

        // 아이템 정보 업데이트 (감사 시간 필드는 JPA Auditing으로 자동 처리)
        item.updateWithEvent(
                request.getName(),
                request.getCategory(),
                request.getImageName(),
                request.getImageUrl(),
                request.getPrice(),
                request.getUnlockLevel(),
                request.isLimited(),
                event
        );

        return characterConverter.toAdminItemResponse(item);
    }

    @Transactional
    public void deleteItem(Long itemId) {
        CharacterItem item = characterItemRepository.findById(itemId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.ITEM_NOT_FOUND));
        
        // S3에서 이미지 삭제
        if (item.getImageName() != null && !item.getImageName().isEmpty()) {
            try {
                s3Service.deleteFile(item.getImageName());
            } catch (Exception e) {
                log.error("아이템 이미지 S3 삭제 실패 - itemId: {}, imageName: {}, 오류: {}", itemId, item.getImageName(), e.getMessage());
                // S3 삭제 실패해도 DB에서는 삭제 진행 (로그만 남김)
            }
        }
        
        characterItemRepository.delete(item);
    }
}
