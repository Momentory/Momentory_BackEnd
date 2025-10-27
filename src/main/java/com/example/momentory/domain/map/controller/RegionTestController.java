package com.example.momentory.domain.map.controller;

import com.example.momentory.domain.map.service.KakaoMapService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class RegionTestController {

    private final KakaoMapService kakaoMapService;

    @GetMapping("/region")
    public String testRegion(@RequestParam("lat") double lat,
                             @RequestParam("lng") double lng) {
        String region = kakaoMapService.getRegionName(lat, lng);
        return "해당 좌표의 행정구역: " + region;
    }
}
