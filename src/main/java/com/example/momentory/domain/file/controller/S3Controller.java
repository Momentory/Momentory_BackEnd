package com.example.momentory.domain.file.controller;

import com.example.momentory.domain.file.dto.S3DeleteRequestDto;
import com.example.momentory.domain.file.dto.S3BulkDeleteRequestDto;
import com.example.momentory.domain.file.dto.S3FileResponseDto;
import com.example.momentory.domain.file.service.S3Service;
import com.example.momentory.global.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/image")
@RequiredArgsConstructor
@Tag(name = "Image S3 API", description = "S3 이미지 단건/다건 업로드/삭제 API")
public class S3Controller {

    private final S3Service s3Service;

    @PostMapping(value = "/upload",  consumes = "multipart/form-data")
    @Operation(summary = "이미지 단건 업로드", description = "Form field: image. 단일 이미지를 업로드하고 imageName, imageUrl을 반환합니다.")
    public ApiResponse<S3FileResponseDto> uploadImage(@RequestParam("image") MultipartFile image) throws IOException {
        return ApiResponse.onSuccess(s3Service.uploadImage(image));
    }

    @PostMapping(value = "/upload/batch", consumes = "multipart/form-data")
    @Operation(summary = "이미지 다건 업로드", description = "Form field: images. 여러 이미지를 한번에 업로드하고 imageName, imageUrl 리스트를 반환합니다.")
    public ApiResponse<List<S3FileResponseDto>> uploadImages(@RequestParam("images") List<MultipartFile> images) throws IOException {
        return ApiResponse.onSuccess(s3Service.uploadImages(images));
    }

    @DeleteMapping("/{imageName}")
    @Operation(summary = "이미지 단건 삭제", description = "PathVariable로 이미지 이름을 받아 삭제합니다.")
    public ApiResponse<String> deleteFile(@PathVariable String imageName) {
        s3Service.deleteFile(imageName);
        return ApiResponse.onSuccess("Deleted: " + imageName);
    }


    @DeleteMapping("")
    @Operation(summary = "이미지 다건 삭제", description = "Body: { imageNames: [] }. 여러 이미지를 한번에 삭제합니다.")
    public ApiResponse<String> deleteFiles(@RequestBody S3BulkDeleteRequestDto request) {
        int deleted = s3Service.deleteFiles(request.getImageNames());
        return ApiResponse.onSuccess("Deleted count: " + deleted);
    }
}
