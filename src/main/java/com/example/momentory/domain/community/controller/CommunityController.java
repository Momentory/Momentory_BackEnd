package com.example.momentory.domain.community.controller;

import com.example.momentory.domain.community.service.CommunityService;
import com.example.momentory.domain.community.dto.CommentRequestDto;
import com.example.momentory.domain.community.dto.CommentResponseDto;
import com.example.momentory.domain.community.entity.Comment;
import com.example.momentory.global.ApiResponse;
import com.example.momentory.global.security.SecurityUtils; // 🚨 SecurityUtils import 추가 (가정)
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal; // 주석 처리된 부분 정리

import java.util.List;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
@Tag(name = "커뮤니티 API", description = "게시글 및 좋아요/댓글 관련 API")
public class CommunityController {

    private final CommunityService communityService;

    // 🚨 Helper 메서드: Comment 엔티티를 Response DTO로 변환
    private CommentResponseDto.CommentDto convertToCommentDto(Comment comment) {
        return CommentResponseDto.CommentDto.builder()
                .commentId(comment.getCommentId())
                .userId(comment.getUser().getId())
                .userNickname(comment.getUser().getNickname())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }

    // --- 기존 좋아요 토글 API 유지 (수정) ---
    @PostMapping("/{postId}/like")
    @Operation(summary = "게시글 좋아요 토글", description = "특정 게시글에 좋아요를 설정하거나 취소합니다.")
    public ApiResponse<String> toggleLike(@PathVariable Long postId) {

        // 🚨 SecurityUtils를 사용하여 userId 가져오도록 수정
        Long userId = SecurityUtils.getCurrentUserId();

        boolean isLiked = communityService.toggleLike(userId, postId);

        if (isLiked) {
            return ApiResponse.onSuccess("게시글에 좋아요를 설정했습니다.");
        } else {
            return ApiResponse.onSuccess("게시글의 좋아요를 취소했습니다.");
        }
    }

    // ----------------------------------------------------------------------
    // 🚨🚨 댓글 (Comment) CRUD API 수정: SecurityUtils 적용 🚨🚨
    // ----------------------------------------------------------------------

    /**
     * 1. 댓글 생성 API (POST /api/v1/posts/{postId}/comments)
     */
    @PostMapping("/{postId}/comments")
    @Operation(summary = "댓글 생성", description = "특정 게시글에 새 댓글을 작성합니다.")
    public ApiResponse<CommentResponseDto.CommentDto> createComment(
            @PathVariable Long postId,
            @RequestBody CommentRequestDto.CreateCommentDto request) {

        // 🚨 SecurityUtils를 사용하여 userId 가져오도록 수정
        Long userId = SecurityUtils.getCurrentUserId();

        Comment comment = communityService.createComment(userId, postId, request);

        return ApiResponse.onSuccess(convertToCommentDto(comment));
    }

    /**
     * 2. 댓글 목록 조회 API (GET /api/v1/posts/{postId}/comments)
     */
    @GetMapping("/{postId}/comments")
    @Operation(summary = "댓글 목록 조회", description = "특정 게시글의 모든 댓글을 조회합니다.")
    public ApiResponse<List<CommentResponseDto.CommentDto>> getComments(@PathVariable Long postId) {
        // 인증이 필요 없는 API이므로 userId 로직은 없습니다.
        List<CommentResponseDto.CommentDto> comments = communityService.getComments(postId);
        return ApiResponse.onSuccess(comments);
    }

    /**
     * 3. 댓글 수정 API (PUT /api/v1/posts/comments/{commentId})
     */
    @PutMapping("/comments/{commentId}")
    @Operation(summary = "댓글 수정", description = "작성된 댓글의 내용을 수정합니다.")
    public ApiResponse<CommentResponseDto.CommentDto> updateComment(
            @PathVariable Long commentId,
            @RequestBody CommentRequestDto.UpdateCommentDto request) {

        // 🚨 SecurityUtils를 사용하여 userId 가져오도록 수정
        Long userId = SecurityUtils.getCurrentUserId();

        Comment comment = communityService.updateComment(commentId, userId, request);
        return ApiResponse.onSuccess(convertToCommentDto(comment));
    }

    /**
     * 4. 댓글 삭제 API (DELETE /api/v1/posts/comments/{commentId})
     */
    @DeleteMapping("/comments/{commentId}")
    @Operation(summary = "댓글 삭제", description = "작성된 댓글을 삭제합니다.")
    public ApiResponse<String> deleteComment(
            @PathVariable Long commentId) {

        // 🚨 SecurityUtils를 사용하여 userId 가져오도록 수정
        Long userId = SecurityUtils.getCurrentUserId();

        communityService.deleteComment(commentId, userId);
        return ApiResponse.onSuccess("댓글이 삭제되었습니다.");
    }
}