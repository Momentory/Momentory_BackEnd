package com.example.momentory.domain.home.service;

import com.example.momentory.domain.character.converter.CharacterConverter;
import com.example.momentory.domain.character.dto.CharacterDto;
import com.example.momentory.domain.character.entity.Character;
import com.example.momentory.domain.character.service.CharacterService;
import com.example.momentory.domain.home.dto.HomeDto;
import com.example.momentory.domain.map.service.CulturalSpotService;
import com.example.momentory.domain.photo.entity.Photo;
import com.example.momentory.domain.photo.repository.PhotoRepository;
import com.example.momentory.domain.photo.entity.Visibility;
import com.example.momentory.domain.user.entity.User;
import com.example.momentory.domain.user.repository.UserProfileRepository;
import com.example.momentory.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class HomeService {

    private final CulturalSpotService culturalSpotService;
    private final PhotoRepository photoRepository;
    private final CharacterService characterService;
    private final UserProfileRepository userProfileRepository;
    private final UserService userService;
    private final CharacterConverter characterConverter;

    public List<HomeDto.TravelSpotSummary> getTop3TravelSpots() {
        List<Map<String, String>> spots = culturalSpotService.getTop3GyeonggiSpots();
        return spots.stream()
                .limit(3)
                .map(s -> HomeDto.TravelSpotSummary.builder()
                        .name(s.get("name"))
                        .type(s.get("type"))
                        .region(s.get("region"))
                        .address(s.get("address"))
                        .tel(s.get("tel"))
                        .imageUrl(s.get("imageUrl"))
                        .build())
                .collect(Collectors.toList());
    }

    public List<HomeDto.RecentPhotoSummary> getRecentPhotos() {
        Pageable pageable = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "createdAt"));
        return photoRepository.findAll(pageable).getContent().stream()
                .filter(p -> p.getVisibility() == null || p.getVisibility() == Visibility.PUBLIC)
                .map(this::toRecentPhotoSummary)
                .collect(Collectors.toList());
    }

    private HomeDto.RecentPhotoSummary toRecentPhotoSummary(Photo p) {
        String nickname = null;
        if (p.getUser() != null) {
            nickname = userProfileRepository.findByUser(p.getUser())
                    .map(profile -> profile.getNickname())
                    .orElse(null);
        }
        return HomeDto.RecentPhotoSummary.builder()
                .photoId(p.getPhotoId())
                .imageUrl(p.getImageUrl())
                .address(p.getAddress())
                .ownerNickname(nickname)
                .build();
    }

    public CharacterDto.CurrentCharacterResponse getMyCharacterStatus() {
        // CharacterService의 새로운 메서드를 사용하여 레벨 상세 정보 포함
        return characterService.getCurrentCharacterWithLevelInfo();
    }

    public List<HomeDto.EventSummary> getUpcomingEvents() {
        // TourAPI 축제(Event) 조회: 오늘 이후 시작하는 항목 위주로 정렬 및 상위 5개 내림
        LocalDate today = LocalDate.now();
        List<Map<String, String>> festivals = culturalSpotService.searchFestivalsFrom(today.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
        return festivals.stream()
                .limit(5)
                .map(m -> HomeDto.EventSummary.builder()
                        .title(m.get("title"))
                        .startDate(parseDate(m.get("eventStartDate")))
                        .endDate(parseDate(m.get("eventEndDate")))
                        .region(m.get("region"))
                        .imageUrl(m.get("firstimage"))
                        .build())
                .collect(Collectors.toList());
    }

    private LocalDate parseDate(String yyyymmdd) {
        try {
            if (yyyymmdd == null || yyyymmdd.isEmpty()) return null;
            return LocalDate.parse(yyyymmdd, DateTimeFormatter.ofPattern("yyyyMMdd"));
        } catch (Exception e) {
            log.warn("Failed to parse date: {}", yyyymmdd);
            return null;
        }
    }
}


