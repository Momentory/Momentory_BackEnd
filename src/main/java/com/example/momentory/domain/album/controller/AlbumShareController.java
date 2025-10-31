package com.example.momentory.domain.album.controller;

import com.example.momentory.domain.album.dto.AlbumResponseDto;
import com.example.momentory.domain.album.service.AlbumService;
import com.example.momentory.global.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/albums")
@RequiredArgsConstructor
@Tag(name = "앨범 공유 API", description = "앨범 공유 링크 관련 API (비로그인 접근 가능)")
public class AlbumShareController {

    private final AlbumService albumService;

    @PostMapping("/{albumId}/share")
    @Operation(summary = "앨범 공유 링크 생성", description = "앨범에 대한 공유 링크를 생성합니다. 프론트 주소(momentory.vercel.app)로 링크가 생성됩니다.")
    public ApiResponse<AlbumResponseDto.ShareUrlResponse> createShareLink(@PathVariable Long albumId) {
        return ApiResponse.onSuccess(albumService.createShareLink(albumId));
    }

    @GetMapping("/share/{shareUuid}")
    @Operation(summary = "공유 앨범 조회", description = "UUID를 통해 공유된 앨범을 조회합니다. 비로그인 사용자도 접근 가능합니다.")
    public ApiResponse<AlbumResponseDto.SharedAlbumResponse> getSharedAlbum(@PathVariable String shareUuid) {
        return ApiResponse.onSuccess(albumService.getSharedAlbum(shareUuid));
    }

    @PatchMapping("/{albumId}/unshare")
    @Operation(summary = "앨범 공유 해제", description = "앨범의 공유를 해제합니다. 링크 접근 시 404 응답이 반환됩니다.")
    public ApiResponse<Void> unshareAlbum(@PathVariable Long albumId) {
        albumService.unshareAlbum(albumId);
        return ApiResponse.onSuccess(null);
    }
}

