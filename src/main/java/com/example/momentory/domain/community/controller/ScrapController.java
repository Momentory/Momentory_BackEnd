package com.example.momentory.domain.community.controller;

import com.example.momentory.domain.community.entity.Post;
import com.example.momentory.domain.community.service.ScrapService;
import com.example.momentory.global.ApiResponse;
import com.example.momentory.global.security.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/community")
@RequiredArgsConstructor
@Tag(name = "커뮤니티 - 스크랩", description = "게시글 스크랩 관련 API")
public class ScrapController {

    private final ScrapService scrapService;

    @PostMapping("/posts/{postId}/scrap")
    @Operation(summary = "게시글 스크랩 토글", description = "게시글을 스크랩 또는 취소합니다.")
    public ApiResponse<String> toggleScrap(@PathVariable Long postId) {
        Long userId = SecurityUtils.getCurrentUserId();
        return ApiResponse.onSuccess(
                scrapService.toggleScrap(userId, postId)
                        ? "게시글을 스크랩했습니다." : "게시글 스크랩을 취소했습니다."
        );
    }

    @GetMapping("/users/me/scraps")
    @Operation(summary = "내가 스크랩한 게시글 조회", description = "현재 인증된 사용자가 스크랩한 게시글 목록을 조회합니다.")
    public ApiResponse<List<Post>> getUserScraps() {
        Long userId = SecurityUtils.getCurrentUserId();
        return ApiResponse.onSuccess(scrapService.getUserScrapList(userId));
    }
}
