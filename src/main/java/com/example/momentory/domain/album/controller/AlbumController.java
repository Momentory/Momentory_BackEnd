package com.example.momentory.domain.album.controller;

import com.example.momentory.domain.album.dto.AlbumRequestDto;
import com.example.momentory.domain.album.dto.AlbumResponseDto;
import com.example.momentory.domain.album.service.AlbumService;
import com.example.momentory.global.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mypage/albums")
@RequiredArgsConstructor
@Tag(name = "앨범 API", description = "앨범 생성/조회/수정 API")
public class AlbumController {

    private final AlbumService albumService;

    @PostMapping("")
    @Operation(summary = "앨범 생성", description = "제목과 이미지 리스트(각 이미지에 index 포함)로 앨범을 생성합니다.")
    public ApiResponse<AlbumResponseDto.AlbumBasicInfo> createAlbum(@RequestBody AlbumRequestDto.CreateAlbum request) {
        return ApiResponse.onSuccess(albumService.createAlbum(request));
    }

    @GetMapping("")
    @Operation(summary = "내 앨범 목록 조회", description = "내 앨범 전체 리스트를 조회합니다.")
    public ApiResponse<List<AlbumResponseDto.AlbumListItem>> getMyAlbums() {
        return ApiResponse.onSuccess(albumService.getMyAlbums());
    }

    @GetMapping("/{albumId}")
    @Operation(summary = "앨범 상세 조회", description = "특정 앨범과 포함된 이미지 목록을 index 순서대로 조회합니다.")
    public ApiResponse<AlbumResponseDto.AlbumDetail> getAlbumDetail(@PathVariable Long albumId) {
        return ApiResponse.onSuccess(albumService.getAlbumDetail(albumId));
    }

    @PatchMapping("/{albumId}")
    @Operation(summary = "앨범 수정", description = "앨범 제목 또는 이미지 목록을 수정합니다. images를 포함하면 전체 이미지 리스트로 교체됩니다 (유지할 기존 이미지 + 새로 추가할 이미지를 모두 포함). 각 이미지의 index로 순서를 지정할 수 있습니다.")
    public ApiResponse<AlbumResponseDto.AlbumBasicInfo> updateAlbum(@PathVariable Long albumId,
                                           @RequestBody AlbumRequestDto.UpdateAlbum request) {
        return ApiResponse.onSuccess(albumService.updateAlbum(albumId, request));
    }
}


