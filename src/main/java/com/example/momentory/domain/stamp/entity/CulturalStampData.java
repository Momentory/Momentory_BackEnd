package com.example.momentory.domain.stamp.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CulturalStampData {

    @Getter
    private static final List<CulturalStamp> STAMPS = List.of(
            new CulturalStamp("수원화성", "수원시"),
            new CulturalStamp("남한산성", "성남시"),
            new CulturalStamp("행복로", "의정부시"),
            new CulturalStamp("안양천", "안양시"),
            new CulturalStamp("부천만화박물관", "부천시"),
            new CulturalStamp("광명동굴", "광명시"),
            new CulturalStamp("평택항", "평택시"),
            new CulturalStamp("동두천계곡", "동두천시"),
            new CulturalStamp("안산누에섬", "안산시"),
            new CulturalStamp("고양킨텍스", "고양시"),
            new CulturalStamp("과천서울대공원", "과천시"),
            new CulturalStamp("구리시한강유채꽃", "구리시"),
            new CulturalStamp("남양주시물의정원", "남양주시"),
            new CulturalStamp("오산독산성", "오산시")
    );

    @Getter
    @AllArgsConstructor
    public static class CulturalStamp {
        private final String name;
        private final String regionName;
    }
}
