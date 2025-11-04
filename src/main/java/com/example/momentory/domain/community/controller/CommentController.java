package com.example.momentory.domain.community.controller;

import com.example.momentory.domain.community.converter.CommunityConverter;
import com.example.momentory.domain.community.dto.CommentRequestDto;
import com.example.momentory.domain.community.dto.CommentResponseDto;
import com.example.momentory.domain.community.entity.Comment;
import com.example.momentory.domain.community.service.CommentService;
import com.example.momentory.global.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/community/posts")
@RequiredArgsConstructor
@Tag(name = "커뮤니티 - 댓글", description = "댓글 CRUD 관련 API")
public class CommentController {

    private final CommentService commentService;
    private final CommunityConverter converter;

    @PostMapping("/{postId}/comments")
    @Operation(summary = "댓글 생성", description = "게시글에 댓글을 작성합니다.")
    public ApiResponse<CommentResponseDto.CommentDto> createComment(
            @PathVariable Long postId,
            @RequestBody CommentRequestDto.CreateCommentDto request) {
        Comment comment = commentService.createComment(postId, request);
        return ApiResponse.onSuccess(converter.toCommentDto(comment));
    }

    @GetMapping("/{postId}/comments")
    @Operation(summary = "댓글 목록 조회", description = "특정 게시글의 댓글을 조회합니다.")
    public ApiResponse<CommentResponseDto.CommentCursorResponse> getComments(
            @PathVariable Long postId,
            @RequestParam(required = false) LocalDateTime cursor,
            @RequestParam(defaultValue = "20") Integer size) {
        CommentRequestDto.CommentCursorRequest req = new CommentRequestDto.CommentCursorRequest(cursor, size);
        return ApiResponse.onSuccess(commentService.getComments(postId, req));
    }

    @PutMapping("/comments/{commentId}")
    @Operation(summary = "댓글 수정", description = "댓글 내용을 수정합니다.")
    public ApiResponse<CommentResponseDto.CommentDto> updateComment(
            @PathVariable Long commentId,
            @RequestBody CommentRequestDto.UpdateCommentDto request) {
        Comment comment = commentService.updateComment(commentId, request);
        return ApiResponse.onSuccess(converter.toCommentDto(comment));
    }

    @DeleteMapping("/comments/{commentId}")
    @Operation(summary = "댓글 삭제", description = "댓글을 삭제합니다.")
    public ApiResponse<String> deleteComment(@PathVariable Long commentId) {
        commentService.deleteComment(commentId);
        return ApiResponse.onSuccess("댓글이 삭제되었습니다.");
    }
}
