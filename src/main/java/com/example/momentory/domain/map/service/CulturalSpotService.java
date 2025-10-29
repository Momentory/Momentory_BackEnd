package com.example.momentory.domain.map.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class CulturalSpotService {

    @Value("${tourapi.key}")
    private String tourApiKey;

    private static final String BASE_URL_V2 = "https://apis.data.go.kr/B551011/KorService2";


    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 근처 문화시설 500m 이내 탐색 (가장 가까운 1개 반환)
     */
    public Optional<Map<String, String>> getNearbyCulturalSpot(double lat, double lon) {
        try {
            String url = String.format(
                    "https://apis.data.go.kr/B551011/KorService2/locationBasedList2" +
                            "?serviceKey=%s&mapX=%f&mapY=%f&radius=%d&arrange=E&MobileOS=ETC&MobileApp=Momentory&_type=json",
                    tourApiKey, lon, lat, 500
            );

            log.info("[TourAPI 요청] 위치 기반 문화시설 조회 URL: {}", url);

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response =
                    restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(new HttpHeaders()), String.class);

            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode items = root.path("response").path("body").path("items").path("item");

            if (items.isEmpty()) {
                log.warn("[TourAPI 응답] 주변 문화시설 없음 (lat={}, lon={})", lat, lon);
                return Optional.empty();
            }

            log.info("[TourAPI 응답] 총 {}개 장소 수신", items.size());

            List<Map<String, String>> nearbySpots = new ArrayList<>();

            for (JsonNode item : items) {
                String title = item.path("title").asText();
                String contentTypeId = item.path("contenttypeid").asText();
                String addr = item.path("addr1").asText();
                double dist = item.path("dist").asDouble();

                // ✅ 거리 제한: 300m 이내만 유효
                if (dist > 300) {
                    log.debug("[TourAPI] 제외: {} (거리 {}m, 300m 초과)", title, String.format("%.1f", dist));
                    continue;
                }

                // 문화/생활문화형만 필터링
                if (List.of("12", "14", "15", "38", "39").contains(contentTypeId)) {
                    String type = mapContentTypeToStampType(contentTypeId);
                    if (type != null) {
                        Map<String, String> spot = Map.of(
                                "name", title,
                                "type", type,
                                "region", extractRegionName(addr),
                                "distance", String.format("%.1f", dist)
                        );
                        nearbySpots.add(spot);
                    }
                }
            }

            if (nearbySpots.isEmpty()) {
                log.info("[TourAPI] 300m 이내 문화형 시설 없음 (lat={}, lon={})", lat, lon);
                return Optional.empty();
            }

            Map<String, String> selected = nearbySpots.get(0);
            log.info("[TourAPI 선택결과] {} ({} / {} / {}m)",
                    selected.get("name"),
                    selected.get("type"),
                    selected.get("region"),
                    selected.get("distance"));

            return Optional.of(selected);

        } catch (Exception e) {
            log.error("[TourAPI 오류] CulturalSpotService.getNearbyCulturalSpot 실패: {}", e.getMessage(), e);
            return Optional.empty();
        }
    }


    /**
     * 장소명으로 타입과 지역 재조회 (스탬프 발급 시 사용)
     */
    public Optional<Map<String, String>> getSpotInfoByName(String spotName) {
        try {
            String encoded = URLEncoder.encode(spotName, StandardCharsets.UTF_8);
            String url = String.format(
                    "https://apis.data.go.kr/B551011/KorService2/searchKeyword2" +
                            "?serviceKey=%s&keyword=%s&MobileOS=ETC&MobileApp=Momentory&_type=json",
                    tourApiKey, encoded
            );

            log.info("[TourAPI 요청] 장소명 검색: {}", spotName);

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response =
                    restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(new HttpHeaders()), String.class);

            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode items = root.path("response").path("body").path("items").path("item");

            if (items.isEmpty()) {
                log.warn("[TourAPI 응답] '{}' 관련 장소 없음", spotName);
                return Optional.empty();
            }

            JsonNode item = items.get(0);
            String contentTypeId = item.path("contenttypeid").asText();
            String addr = item.path("addr1").asText();
            String type = mapContentTypeToStampType(contentTypeId);

            log.info("[TourAPI 응답] {} → type={}, region={}", spotName, type, extractRegionName(addr));

            if (type == null) return Optional.empty();

            return Optional.of(Map.of(
                    "type", type,
                    "region", extractRegionName(addr)
            ));

        } catch (Exception e) {
            log.error("[TourAPI 오류] CulturalSpotService.getSpotInfoByName 실패: {}", e.getMessage(), e);
            return Optional.empty();
        }
    }

    /**
     * TourAPI contentTypeId → 내부 StampType 매핑
     */
    private String mapContentTypeToStampType(String contentTypeId) {
        return switch (contentTypeId) {
            case "12", "14" -> "CULTURAL_HERITAGE"; // 관광지, 문화시설
            case "15" -> "FESTIVAL";                // 축제
            case "38" -> "LANDMARK";                // 쇼핑/전통시장
            case "39" -> "FOOD";                    // 음식문화
            default -> null;
        };
    }

    /**
     * 업로드 후 관광지 추천 (거리 제한 없이 랜덤 10개)
     */
    public List<Map<String, String>> getRecommendedSpots(double lat, double lon) {
        try {
            StringBuilder urlBuilder = new StringBuilder(BASE_URL_V2 + "/locationBasedList2");
            urlBuilder.append("?serviceKey=").append(tourApiKey);
            urlBuilder.append("&MobileOS=WIN");
            urlBuilder.append("&MobileApp=").append("momentory");
            urlBuilder.append("&_type=json");
            urlBuilder.append("&mapX=").append(lon);  // 경도
            urlBuilder.append("&mapY=").append(lat);   // 위도
            urlBuilder.append("&radius=").append(10000); // 기본 1km
            urlBuilder.append("&numOfRows=100");
            urlBuilder.append("&pageNo=1");
            urlBuilder.append("&arrange=E"); // 거리순 정렬

            log.info("[TourAPI 요청] {}", urlBuilder.toString());

            String url = urlBuilder.toString();

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response =
                    restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(new HttpHeaders()), String.class);

            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode items = root.path("response").path("body").path("items").path("item");

            if (items.isEmpty()) return List.of();

            List<Map<String, String>> allSpots = new ArrayList<>();
            for (JsonNode item : items) {
                allSpots.add(Map.of(
                        "name", item.path("title").asText(),
                        "type", mapContentTypeToStampType(item.path("contenttypeid").asText()),
                        "region", extractRegionName(item.path("addr1").asText()),
                        "address", item.path("addr1").asText(),
                        "tel", item.path("tel").asText(),
                        "imageUrl", item.path("firstimage").asText()
                ));
            }

            Collections.shuffle(allSpots);
            return allSpots.stream().limit(10).toList();

        } catch (Exception e) {
            log.error("[TourAPI 오류] {}", e.getMessage(), e);
            return List.of();
        }
    }



    private String extractRegionName(String address) {
        if (address == null || address.isEmpty()) return "기타";
        String[] parts = address.split(" ");
        return parts.length >= 2 ? parts[0] + " " + parts[1] : parts[0];
    }
}
