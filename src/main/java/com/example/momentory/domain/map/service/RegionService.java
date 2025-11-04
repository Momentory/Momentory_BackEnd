package com.example.momentory.domain.map.service;

import com.example.momentory.domain.map.converter.RegionConverter;
import com.example.momentory.domain.map.dto.RegionResponseDto;
import com.example.momentory.domain.map.entity.Region;
import com.example.momentory.domain.map.repository.RegionRepository;
import com.example.momentory.global.code.status.ErrorStatus;
import com.example.momentory.global.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RegionService {

    private final RegionRepository regionRepository;
    private final RegionConverter regionConverter;

    /**
     * 모든 지역 정보 조회
     */
    public List<RegionResponseDto.RegionDto> getAllRegions() {
        List<Region> regions = regionRepository.findAll();
        return regionConverter.toRegionDtoList(regions);
    }

    /**
     * 특정 지역 정보 조회 (ID)
     */
    public RegionResponseDto.RegionDto getRegionById(Long regionId) {
        Region region = regionRepository.findById(regionId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.RESOURCE_NOT_FOUND));
        return regionConverter.toRegionDto(region);
    }

    /**
     * 특정 지역 정보 조회 (이름)
     */
    public RegionResponseDto.RegionDto getRegionByName(String regionName) {
        Region region = regionRepository.findByName(regionName)
                .orElseThrow(() -> new GeneralException(ErrorStatus.RESOURCE_NOT_FOUND));
        return regionConverter.toRegionDto(region);
    }
}