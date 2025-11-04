package com.example.momentory.domain.map.converter;

import com.example.momentory.domain.map.dto.RegionResponseDto;
import com.example.momentory.domain.map.entity.Region;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class RegionConverter {

    public RegionResponseDto.RegionDto toRegionDto(Region region) {
        return RegionResponseDto.RegionDto.builder()
                .id(region.getId())
                .name(region.getName())
                .type(region.getType())
                .code(region.getCode())
                .build();
    }

    public List<RegionResponseDto.RegionDto> toRegionDtoList(List<Region> regions) {
        return regions.stream()
                .map(this::toRegionDto)
                .collect(Collectors.toList());
    }
}