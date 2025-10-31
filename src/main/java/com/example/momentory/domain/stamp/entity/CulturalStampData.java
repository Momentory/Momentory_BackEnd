package com.example.momentory.domain.stamp.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CulturalStampData {

    @Getter
    private static final List<CulturalStamp> STAMPS = List.of(
            new CulturalStamp("수원 화성", "수원시"),
            new CulturalStamp("남한산성", "성남시"),
            new CulturalStamp("행복로", "의정부시"),
            new CulturalStamp("안양천제방벚꽃길", "안양시"),
            new CulturalStamp("한국만화박물관", "부천시"),
            new CulturalStamp("광명 동굴", "광명시"),
            new CulturalStamp("평택항", "평택시"),
            new CulturalStamp("동두천시 계곡", "동두천시"),
            new CulturalStamp("안산 누에섬", "안산시"),
            new CulturalStamp("고양 킨텍스", "고양시"),
            new CulturalStamp("서울대공원", "과천시"),
            new CulturalStamp("구리시 한강유채꽃", "구리시"),
            new CulturalStamp("남양주시", "남양주시"),
            new CulturalStamp("오산 독산성", "오산시")
    );

    @Getter
    @AllArgsConstructor
    public static class CulturalStamp {
        private final String name;
        private final String regionName;
    }
}
