package com.example.momentory.domain.file.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class S3FileResponseDto {
    private String imageName;
    private String imageUrl;
}
