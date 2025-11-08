package com.example.momentory.domain.map.service;

import com.example.momentory.domain.stamp.entity.CulturalStampData;
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
import java.util.stream.Collectors;

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
                    BASE_URL_V2 + "/locationBasedList2" +
                            "?serviceKey=%s&mapX=%f&mapY=%f&radius=%d&arrange=E&MobileOS=ETC&MobileApp=Momentory&_type=json",
                    tourApiKey, lon, lat, 500
            );

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response =
                    restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(new HttpHeaders()), String.class);

            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode items = root.path("response").path("body").path("items").path("item");

            if (items == null || !items.isArray() || items.isEmpty()) {
                log.warn("[TourAPI 응답] 주변 문화시설 없음 (lat={}, lon={})", lat, lon);
                return Optional.empty();
            }

            List<Map<String, String>> nearbySpots = new ArrayList<>();

            for (JsonNode item : items) {
                String title = item.path("title").asText("");
                String contentTypeId = item.path("contenttypeid").asText("");
                String addr = item.path("addr1").asText("");
                double dist = item.path("dist").asDouble(0);

                if (title.isBlank()) continue;
                if (dist > 500) continue; // 반경 제한
                if (!List.of("12", "14", "15", "38", "39").contains(contentTypeId)) continue;

                String type = mapContentTypeToStampType(contentTypeId);
                if (type == null) continue;

                Map<String, String> spot = new HashMap<>();
                spot.put("name", title);
                spot.put("type", type);
                spot.put("region", extractRegionName(addr));
                spot.put("distance", String.format("%.1f", dist));
                nearbySpots.add(spot);
            }

            if (nearbySpots.isEmpty()) {
                return Optional.empty();
            }

            List<String> STAMP_SPOTS = CulturalStampData.getSTAMPS()
                    .stream()
                    .map(CulturalStampData.CulturalStamp::getName)
                    .collect(Collectors.toList());

            Map<String, String> KNOWN_NAME_MAP = Map.ofEntries(
                    Map.entry("수원 화성 [유네스코 세계유산]", "수원 화성"),
                    Map.entry("평택항 홍보관", "평택항"),
                    Map.entry("누에섬 등대전망대", "안산 누에섬"),
                    Map.entry("탄도항누에섬", "안산 누에섬"),
                    Map.entry("킨텍스", "고양 킨텍스"),
                    Map.entry("서울대공원", "과천 서울대공원"),
                    Map.entry("왕방계곡", "동두천 계곡")
            );

            Optional<Map<String, String>> matchedStamp = nearbySpots.stream()
                    .map(spot -> {
                        String name = spot.get("name");
                        String normalized = normalizeName(name);

                        // 사전 매핑 테이블에 있으면 강제 교정
                        if (KNOWN_NAME_MAP.containsKey(name)) {
                            spot.put("name", KNOWN_NAME_MAP.get(name));
                            return spot;
                        }

                        // 유사도 기반 매칭
                        STAMP_SPOTS.stream()
                                .filter(stamp -> similarity(normalizeName(stamp), normalized) >= 0.75)
                                .findFirst()
                                .ifPresent(matched -> spot.put("name", matched));

                        return spot;
                    })
                    .filter(spot -> STAMP_SPOTS.contains(spot.get("name")))
                    .findFirst();

            if (matchedStamp.isPresent()) {
                return matchedStamp;
            }

            Comparator<Map<String, String>> byTypePriority = Comparator.comparingInt(
                    spot -> switch (spot.get("type")) {
                        case "CULTURAL_HERITAGE" -> 1;
                        case "LANDMARK" -> 2;
                        case "FESTIVAL" -> 3;
                        case "FOOD" -> 4;
                        default -> 5;
                    }
            );

            nearbySpots.sort(byTypePriority.thenComparingDouble(
                    spot -> Double.parseDouble(spot.get("distance")))
            );

            Map<String, String> selected = nearbySpots.get(0);

            for (String standard : STAMP_SPOTS) {
                String normalizedStandard = standard.replace(" ", "");
                String normalizedName = selected.get("name").replace(" ", "");
                if (normalizedName.contains(normalizedStandard) || normalizedStandard.contains(normalizedName)) {
                    selected.put("name", standard);
                    break;
                }
            }

            Map<String, double[]> STAMP_COORDS = Map.of(
                    "수원 화성", new double[]{37.285, 127.019},
                    "부천 만화박물관", new double[]{37.504, 126.776},
                    "광명 동굴", new double[]{37.426, 126.864},
                    "고양 킨텍스", new double[]{37.668, 126.745}
                    // 필요한 곳 계속 추가
            );

            for (Map.Entry<String, double[]> entry : STAMP_COORDS.entrySet()) {
                double distance = haversine(lat, lon, entry.getValue()[0], entry.getValue()[1]);
                if (distance <= 500) {
                    selected.put("name", entry.getKey());
                    break;
                }
            }

            return Optional.of(selected);

        } catch (Exception e) {
            log.error("[TourAPI 오류] getNearbyCulturalSpot 실패: {}", e.getMessage(), e);
            return Optional.empty();
        }
    }

    // Haversine 공식으로 두 좌표 간 거리 계산 (미터 단위)
    private double haversine(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371000; // 지구 반경 (m)
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    // 문자열 정규화 (공백, 괄호 제거)
    private String normalizeName(String name) {
        if (name == null) return "";
        return name.replaceAll("\\s+", "")
                .replaceAll("\\(.*?\\)", "")
                .replaceAll("\\[.*?\\]", "")
                .toLowerCase();
    }

    // 문자열 유사도 계산 (Levenshtein distance 기반)
    private double similarity(String s1, String s2) {
        int distance = levenshteinDistance(s1, s2);
        int maxLen = Math.max(s1.length(), s2.length());
        return maxLen == 0 ? 1.0 : 1.0 - (double) distance / maxLen;
    }

    private int levenshteinDistance(String s1, String s2) {
        int[][] dp = new int[s1.length() + 1][s2.length() + 1];
        for (int i = 0; i <= s1.length(); i++) dp[i][0] = i;
        for (int j = 0; j <= s2.length(); j++) dp[0][j] = j;

        for (int i = 1; i <= s1.length(); i++) {
            for (int j = 1; j <= s2.length(); j++) {
                int cost = s1.charAt(i - 1) == s2.charAt(j - 1) ? 0 : 1;
                dp[i][j] = Math.min(
                        Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1),
                        dp[i - 1][j - 1] + cost
                );
            }
        }
        return dp[s1.length()][s2.length()];
    }

    /**
     * 장소명으로 타입과 지역 재조회 (스탬프 발급 시 사용)
     */
    public Optional<Map<String, String>> getSpotInfoByName(String spotName) {
        try {
            if (spotName == null || spotName.isBlank()) return Optional.empty();

            String encoded = URLEncoder.encode(spotName, StandardCharsets.UTF_8);
            String url = String.format(
                    BASE_URL_V2 + "/searchKeyword2?serviceKey=%s&keyword=%s&MobileOS=ETC&MobileApp=Momentory&_type=json",
                    tourApiKey, encoded
            );

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response =
                    restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(new HttpHeaders()), String.class);

            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode items = root.path("response").path("body").path("items").path("item");

            if (items == null || !items.isArray() || items.isEmpty()) {
                log.warn("[TourAPI 응답] '{}' 관련 장소 없음", spotName);
                return Optional.empty();
            }

            JsonNode item = items.get(0);
            String contentTypeId = item.path("contenttypeid").asText("");
            String addr = item.path("addr1").asText("");
            String type = mapContentTypeToStampType(contentTypeId);

            if (type == null) return Optional.empty();

            Map<String, String> result = new HashMap<>();
            result.put("type", type);
            result.put("region", extractRegionName(addr));

            return Optional.of(result);

        } catch (Exception e) {
            log.error("[TourAPI 오류] getSpotInfoByName 실패: {}", e.getMessage(), e);
            return Optional.empty();
        }
    }

    /**
     * TourAPI contentTypeId → 내부 StampType 매핑
     */
    private String mapContentTypeToStampType(String contentTypeId) {
        return switch (contentTypeId) {
            case "12", "14" -> "CULTURAL_HERITAGE"; // 관광지, 문화시설
            case "15" -> "FESTIVAL";
            case "38" -> "LANDMARK";
            case "39" -> "FOOD";
            default -> null;
        };
    }

    /**
     * 업로드 후 관광지 추천 (거리 제한 없이 랜덤 10개)
     */
    public List<Map<String, String>> getRecommendedSpots(double lat, double lon) {
        // 어떤 경로로 호출돼도 항상 4개만 요청/반환
        return getRecommendedSpots(lat, lon, 4);
    }

    /**
     * 추천 관광지 조회 (개수 제한 버전). TourAPI 호출 자체를 limit에 맞춰 줄이고 결과도 limit로 제한.
     */
    public List<Map<String, String>> getRecommendedSpots(double lat, double lon, int limit) {
        try {
            if (limit <= 0) {
                limit = 4;
            }
            String url = String.format(
                    BASE_URL_V2 + "/locationBasedList2?serviceKey=%s&MobileOS=ETC&MobileApp=Momentory&_type=json"
                            + "&mapX=%f&mapY=%f&radius=10000&numOfRows=%d&pageNo=1&arrange=E",
                    tourApiKey, lon, lat, Math.max(limit, 3)
            );


            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response =
                    restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(new HttpHeaders()), String.class);

            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode items = root.path("response").path("body").path("items").path("item");

            if (items == null || !items.isArray() || items.isEmpty()) return List.of();

            List<Map<String, String>> allSpots = new ArrayList<>();

            for (JsonNode item : items) {
                String name = item.path("title").asText("");
                String type = mapContentTypeToStampType(item.path("contenttypeid").asText(""));
                String addr = item.path("addr1").asText("");
                String imageUrl = item.path("firstimage").asText("");
                if (name.isBlank() || type == null) continue;

                Map<String, String> spot = new HashMap<>();
                spot.put("name", name);
                spot.put("type", type);
                spot.put("region", extractRegionName(addr));
                spot.put("address", addr);
                spot.put("tel", item.path("tel").asText(""));
                spot.put("imageUrl", imageUrl);

                allSpots.add(spot);
            }

            return allSpots.stream().limit(limit).toList();

        } catch (Exception e) {
            log.error("[TourAPI 오류] getRecommendedSpots(lat,lon,limit) 실패: {}", e.getMessage(), e);
            return List.of();
        }
    }

    private String extractRegionName(String address) {
        if (address == null || address.isBlank()) return "기타";
        String[] parts = address.trim().split("\\s+");
        return parts.length >= 2 ? parts[0] + " " + parts[1] : parts[0];
    }

    /**
     * 다가오는 축제/행사 조회
     */
    public List<Map<String, String>> searchFestivalsFrom(String startDateYmd) {
        try {
            String url = String.format(
                    BASE_URL_V2 + "/searchFestival2?serviceKey=%s&eventStartDate=%s&areaCode=31"
                            + "&MobileOS=ETC&MobileApp=Momentory&_type=json&arrange=O&numOfRows=50&pageNo=1",
                    tourApiKey, startDateYmd
            );


            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response =
                    restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(new HttpHeaders()), String.class);

            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode items = root.path("response").path("body").path("items").path("item");

            if (items == null || !items.isArray() || items.isEmpty()) return List.of();

            List<Map<String, String>> results = new ArrayList<>();

            for (JsonNode item : items) {
                Map<String, String> data = new HashMap<>();
                data.put("title", item.path("title").asText(""));
                data.put("eventStartDate", item.path("eventstartdate").asText(""));
                data.put("eventEndDate", item.path("eventenddate").asText(""));
                data.put("region", extractRegionName(item.path("addr1").asText("")));
                data.put("firstimage", item.path("firstimage").asText(""));
                results.add(data);
            }

            return results;
        } catch (Exception e) {
            log.error("[TourAPI 오류] searchFestivalsFrom 실패: {}", e.getMessage(), e);
            return List.of();
        }
    }

    /**
     * 경기도 지역의 대표 관광지 Top3 조회
     */
    public List<Map<String, String>> getTop3GyeonggiSpots() {
        try {
            String url = String.format(
                    BASE_URL_V2 + "/areaBasedList2?serviceKey=%s&areaCode=31"
                            + "&MobileOS=ETC&MobileApp=Momentory&_type=json"
                            + "&arrange=O&numOfRows=20&pageNo=1",
                    tourApiKey
            );

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response =
                    restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(new HttpHeaders()), String.class);

            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode items = root.path("response").path("body").path("items").path("item");

            if (items == null || !items.isArray() || items.isEmpty()) return List.of();

            List<Map<String, String>> allSpots = new ArrayList<>();
            for (JsonNode item : items) {
                String type = mapContentTypeToStampType(item.path("contenttypeid").asText(""));
                if (type == null) continue;

                Map<String, String> spot = new HashMap<>();
                spot.put("name", item.path("title").asText(""));
                spot.put("type", type);
                spot.put("region", extractRegionName(item.path("addr1").asText("")));
                spot.put("address", item.path("addr1").asText(""));
                spot.put("tel", item.path("tel").asText(""));
                spot.put("imageUrl", item.path("firstimage").asText(""));

                allSpots.add(spot);
            }

            List<Map<String, String>> filtered = allSpots.stream()
                    .filter(s -> s.get("imageUrl") != null && !s.get("imageUrl").isBlank())
                    .collect(Collectors.toList());

            Collections.shuffle(filtered);
            return filtered.stream().limit(3).toList();

        } catch (Exception e) {
            log.error("[TourAPI 오류] getTop3GyeonggiSpots 실패: {}", e.getMessage(), e);
            return List.of();
        }
    }
}
