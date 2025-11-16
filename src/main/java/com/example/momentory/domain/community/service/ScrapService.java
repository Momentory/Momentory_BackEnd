package com.example.momentory.domain.community.service;

import com.example.momentory.domain.community.converter.CommunityConverter;
import com.example.momentory.domain.community.dto.PostRequestDto;
import com.example.momentory.domain.community.dto.PostResponseDto;
import com.example.momentory.domain.community.entity.Post;
import com.example.momentory.domain.community.entity.Scrap;
import com.example.momentory.domain.community.repository.PostRepository;
import com.example.momentory.domain.community.repository.ScrapRepository;
import com.example.momentory.domain.user.entity.User;
import com.example.momentory.domain.user.service.UserService;
import com.example.momentory.global.code.status.ErrorStatus;
import com.example.momentory.global.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScrapService {

    private final ScrapRepository scrapRepository;
    private final PostRepository postRepository;
    private final UserService userService;
    private final CommunityConverter communityConverter;

    /**
     * 스크랩 토글 (설정/취소)
     */
    @Transactional
    public boolean toggleScrap(Long postId) {
        User user = userService.getCurrentUser();

        // Post 엔티티 조회
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.POST_NOT_FOUND));

        // 기존 스크랩 레코드 존재 확인
        Optional<Scrap> existingScrap = scrapRepository.findByUserAndPost(user, post);

        if (existingScrap.isPresent()) {
            // 스크랩 취소
            scrapRepository.delete(existingScrap.get());
            return false;
        } else {
            // 스크랩 생성
            Scrap newScrap = Scrap.builder()
                    .user(user)
                    .post(post)
                    .build();

            scrapRepository.save(newScrap);
            return true;
        }
    }

    /**
     * 사용자별 스크랩 목록 조회 (postId와 imageUrl만)
     */
    @Transactional(readOnly = true)
    public List<PostResponseDto.PostThumbnailDto> getUserScrapList() {
        User user = userService.getCurrentUser();

        // DB에서 직접 postId와 imageUrl만 조회
        return scrapRepository.findPostThumbnailsByUser(user);
    }

    /**
     * 특정 사용자의 스크랩 목록 조회 (postId와 imageUrl만)
     */
    @Transactional(readOnly = true)
    public List<PostResponseDto.PostThumbnailDto> getSpecificUserScrapList(Long userId) {
        User user = userService.getUserById(userId);

        // DB에서 직접 postId와 imageUrl만 조회
        return scrapRepository.findPostThumbnailsByUser(user);
    }

    /**
     * 통합 스크랩 게시글 조회 (커서 페이지네이션)
     * @param userId null이면 현재 사용자, 값이 있으면 해당 사용자의 스크랩 게시글 조회
     */
    @Transactional(readOnly = true)
    public PostResponseDto.PostThumbnailCursorResponse getUserScrapsWithPagination(
            Long userId, PostRequestDto.PostCursorRequest request) {

        User user = userId == null ? userService.getCurrentUser() : userService.getUserById(userId);

        int size = request.getSize() != null ? request.getSize() : 20;
        Pageable pageable = PageRequest.of(0, size + 1);

        List<PostResponseDto.PostThumbnailDto> posts;
        if (request.getCursor() == null) {
            // 첫 페이지
            posts = scrapRepository.findPostThumbnailsByUserOrderByCreatedAtDesc(user, pageable);
        } else {
            // 커서 이후 데이터
            posts = scrapRepository.findPostThumbnailsByUserWithCursor(user, request.getCursor(), pageable);
        }

        boolean hasNext = posts.size() > size;
        if (hasNext) {
            posts = posts.subList(0, size);
        }

        // 마지막 게시글의 createdAt을 커서로 사용하기 위해 Post 엔티티 조회 필요
        LocalDateTime nextCursor = null;
        if (hasNext && !posts.isEmpty()) {
            Long lastPostId = posts.get(posts.size() - 1).getPostId();
            Post lastPost = postRepository.findById(lastPostId)
                    .orElseThrow(() -> new GeneralException(ErrorStatus.POST_NOT_FOUND));
            nextCursor = lastPost.getCreatedAt();
        }

        return PostResponseDto.PostThumbnailCursorResponse.builder()
                .posts(posts)
                .nextCursor(nextCursor)
                .hasNext(hasNext)
                .build();
    }
}