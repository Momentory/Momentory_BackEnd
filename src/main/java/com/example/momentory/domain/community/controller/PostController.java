package com.example.momentory.domain.community.controller;

import com.example.momentory.domain.community.dto.PostRequestDto;
import com.example.momentory.domain.community.dto.PostResponseDto;
import com.example.momentory.domain.community.service.LikeService;
import com.example.momentory.domain.community.service.PostQueryService;
import com.example.momentory.domain.community.service.PostService;
import com.example.momentory.domain.community.service.ScrapService;
import com.example.momentory.global.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/community/posts")
@RequiredArgsConstructor
@Tag(name = "커뮤니티 - 게시글", description = "게시글 CRUD 및 좋아요 관련 API")
public class PostController {

    private final PostService postService;
    private final LikeService likeService;
    private final PostQueryService postQueryService;
    private final ScrapService scrapService;

    @PostMapping
    @Operation(summary = "게시글 생성", description = "새로운 게시글을 작성합니다.")
    public ApiResponse<PostResponseDto.PostSimpleDto> createPost(@RequestBody PostRequestDto.CreatePostDto request) {
        return ApiResponse.onSuccess(postService.createPost(request));
    }

    @GetMapping("/{postId}")
    @Operation(summary = "게시글 상세 조회", description = "특정 게시글의 상세 정보를 조회합니다.")
    public ApiResponse<PostResponseDto.PostDto> getPost(@PathVariable Long postId) {
        return ApiResponse.onSuccess(postQueryService.getPost(postId));
    }

    @GetMapping
    @Operation(summary = "게시글 전체 조회", description = "커서 페이지네이션 방식으로 모든 게시글을 조회합니다.")
    public ApiResponse<PostResponseDto.PostCursorResponse> getAllPosts(
            @RequestParam(required = false) LocalDateTime cursor,
            @RequestParam(defaultValue = "20") Integer size) {
        PostRequestDto.PostCursorRequest req = new PostRequestDto.PostCursorRequest(cursor, size);
        return ApiResponse.onSuccess(postQueryService.getAllPosts(req));
    }

    @GetMapping("/region/{regionId}")
    @Operation(summary = "지역별 게시글 조회", description = "특정 지역 게시글을 커서 페이지네이션으로 조회합니다.")
    public ApiResponse<PostResponseDto.PostCursorResponse> getPostsByRegion(
            @PathVariable Long regionId,
            @RequestParam(required = false) LocalDateTime cursor,
            @RequestParam(defaultValue = "20") Integer size) {
        PostRequestDto.PostCursorRequest req = new PostRequestDto.PostCursorRequest(cursor, size);
        return ApiResponse.onSuccess(postQueryService.getPostsByRegion(regionId, req));
    }

    @GetMapping("/tag/{tagName}")
    @Operation(summary = "태그별 게시글 조회", description = "특정 태그가 포함된 게시글을 조회합니다.")
    public ApiResponse<PostResponseDto.PostCursorResponse> getPostsByTag(
            @PathVariable String tagName,
            @RequestParam(required = false) LocalDateTime cursor,
            @RequestParam(defaultValue = "20") Integer size) {
        PostRequestDto.PostCursorRequest req = new PostRequestDto.PostCursorRequest(cursor, size);
        return ApiResponse.onSuccess(postQueryService.getPostsByTag(tagName, req));
    }

    @GetMapping("/tags/filter")
    @Operation(summary = "다중 태그 필터링", description = "여러 태그로 게시글을 필터링합니다. (OR 조건)")
    public ApiResponse<PostResponseDto.PostCursorResponse> getPostsByTags(
            @RequestParam List<String> tags,
            @RequestParam(required = false) LocalDateTime cursor,
            @RequestParam(defaultValue = "20") Integer size) {
        PostRequestDto.PostTagFilterRequest req = PostRequestDto.PostTagFilterRequest.builder()
                .tags(tags)
                .cursor(cursor)
                .size(size)
                .build();
        return ApiResponse.onSuccess(postQueryService.getPostsByTags(req));
    }

    @GetMapping("/search")
    @Operation(summary = "게시글 검색", description = "제목/내용으로 게시글을 검색합니다.")
    public ApiResponse<PostResponseDto.PostCursorResponse> searchPosts(
            @RequestParam String keyword,
            @RequestParam(required = false) LocalDateTime cursor,
            @RequestParam(defaultValue = "20") Integer size) {
        PostRequestDto.PostCursorRequest req = new PostRequestDto.PostCursorRequest(cursor, size);
        return ApiResponse.onSuccess(postQueryService.searchPosts(keyword, req));
    }

    @PutMapping("/{postId}")
    @Operation(summary = "게시글 수정", description = "게시글을 수정합니다.")
    public ApiResponse<PostResponseDto.PostSimpleDto> updatePost(
            @PathVariable Long postId,
            @RequestBody PostRequestDto.UpdatePostDto request) {
        return ApiResponse.onSuccess(postService.updatePost(postId, request));
    }

    @DeleteMapping("/{postId}")
    @Operation(summary = "게시글 삭제", description = "게시글을 삭제합니다.")
    public ApiResponse<String> deletePost(@PathVariable Long postId) {
        postService.deletePost(postId);
        return ApiResponse.onSuccess("게시글이 삭제되었습니다.");
    }

    @PostMapping("/{postId}/like")
    @Operation(summary = "게시글 좋아요 토글", description = "좋아요 설정 또는 취소.")
    public ApiResponse<String> toggleLike(@PathVariable Long postId) {
        return ApiResponse.onSuccess(
                likeService.toggleLike(postId)
                        ? "게시글에 좋아요를 설정했습니다." : "게시글의 좋아요를 취소했습니다."
        );
    }

    @PostMapping("/posts/{postId}/scrap")
    @Operation(summary = "게시글 스크랩 토글", description = "게시글을 스크랩 또는 취소합니다.")
    public ApiResponse<String> toggleScrap(@PathVariable Long postId) {
        return ApiResponse.onSuccess(
                scrapService.toggleScrap(postId)
                        ? "게시글을 스크랩했습니다." : "게시글 스크랩을 취소했습니다."
        );
    }

}
