package com.example.momentory.domain.file.controller;

import com.example.momentory.domain.file.dto.S3DeleteRequestDto;
import com.example.momentory.domain.file.dto.S3FileResponseDto;
import com.example.momentory.domain.file.service.S3Service;
import com.example.momentory.global.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/image")
@RequiredArgsConstructor
@Tag(name = "Image S3 API", description = "image 업로드 API")
public class S3Controller {

    private final S3Service s3Service;

    /**
     * 파일 업로드
     */
    @PostMapping(value = "/upload",  consumes = "multipart/form-data")
    public ApiResponse<S3FileResponseDto> uploadImage(@RequestParam("file") MultipartFile file) throws IOException {
        return ApiResponse.onSuccess(s3Service.uploadImage(file));
    }

    /**
     * 파일 삭제
     */
    @DeleteMapping("/delete")
    public ApiResponse<String> deleteFile(@RequestBody S3DeleteRequestDto request) {
        s3Service.deleteFile(request.getImageName());
        return ApiResponse.onSuccess("Deleted: " + request.getImageName());
    }
}
