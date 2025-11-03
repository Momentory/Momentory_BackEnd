package com.example.momentory.domain.character.service;

import com.example.momentory.domain.character.converter.CharacterConverter;
import com.example.momentory.domain.character.dto.AdminItemDto;
import com.example.momentory.domain.character.entity.CharacterItem;
import com.example.momentory.domain.character.repository.CharacterItemRepository;
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
    private final CharacterConverter characterConverter;
    private final S3Service s3Service;

    @Transactional
    public AdminItemDto.Response createItem(AdminItemDto.CreateRequest request) {
        CharacterItem item = CharacterItem.builder()
                .name(request.getName())
                .category(request.getCategory())
                .imageName(request.getImageName())
                .imageUrl(request.getImageUrl())
                .price(request.getPrice())
                .unlockLevel(request.getUnlockLevel())
                .build();

        CharacterItem savedItem = characterItemRepository.save(item);
        log.info("아이템 생성 완료 - ID: {}, 이름: {}, imageName: {}", savedItem.getItemId(), savedItem.getName(), savedItem.getImageName());
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
                    log.info("기존 이미지 S3 삭제 완료 - imageName: {}", item.getImageName());
                } catch (Exception e) {
                    log.error("기존 이미지 S3 삭제 실패 - imageName: {}, 오류: {}", item.getImageName(), e.getMessage());
                }
            }
        }

        // 아이템 정보 업데이트 (감사 시간 필드는 JPA Auditing으로 자동 처리)
        item.update(
                request.getName(),
                request.getCategory(),
                request.getImageName(),
                request.getImageUrl(),
                request.getPrice(),
                request.getUnlockLevel()
        );

        log.info("아이템 수정 완료 - ID: {}, imageName: {}", itemId, request.getImageName());
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
                log.info("아이템 이미지 S3 삭제 완료 - itemId: {}, imageName: {}", itemId, item.getImageName());
            } catch (Exception e) {
                log.error("아이템 이미지 S3 삭제 실패 - itemId: {}, imageName: {}, 오류: {}", itemId, item.getImageName(), e.getMessage());
                // S3 삭제 실패해도 DB에서는 삭제 진행 (로그만 남김)
            }
        }
        
        characterItemRepository.delete(item);
        log.info("아이템 DB 삭제 완료 - itemId: {}", itemId);
    }
}
