package com.example.momentory.domain.map.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class KakaoMapService {

    @Value("${kakao.api.key}")
    private String kakaoApiKey;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public String getRegionName(double latitude, double longitude) {
        String url = String.format(
                "https://dapi.kakao.com/v2/local/geo/coord2regioncode.json?x=%f&y=%f",
                longitude, latitude
        );

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + kakaoApiKey);
        headers.set("KA", "sdk/1.0 os/java origin/http://localhost:8080"); // ✅ 추가 핵심
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        try {
            JsonNode root = objectMapper.readTree(response.getBody());
            return root.path("documents").get(0).path("region_2depth_name").asText();
        } catch (Exception e) {
            throw new RuntimeException("JSON 파싱 실패", e);
        }
    }



}
