package com.example.momentory.domain.community.service;

import com.example.momentory.domain.community.converter.CommunityConverter;
import com.example.momentory.domain.community.dto.PostRequestDto;
import com.example.momentory.domain.community.dto.PostResponseDto;
import com.example.momentory.domain.community.entity.Comment;
import com.example.momentory.domain.community.entity.Post;
import com.example.momentory.domain.community.repository.CommentRepository;
import com.example.momentory.domain.community.repository.PostRepository;
import com.example.momentory.domain.map.entity.Region;
import com.example.momentory.domain.map.repository.RegionRepository;
import com.example.momentory.domain.user.entity.User;
import com.example.momentory.domain.user.repository.UserRepository;
import com.example.momentory.global.code.status.ErrorStatus;
import com.example.momentory.global.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostQueryService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final RegionRepository regionRepository;
    private final CommentRepository commentRepository;
    private final CommunityConverter communityConverter;

    /**
     * 게시글 단건 조회
     */
    public PostResponseDto.PostDto getPost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.POST_NOT_FOUND));

        return communityConverter.toPostDto(post);
    }

    /**
     * 전체 게시글 조회 (커서 페이지네이션)
     */
    public PostResponseDto.PostCursorResponse getAllPosts(PostRequestDto.PostCursorRequest request) {
        int size = request.getSize() != null ? request.getSize() : 20;
        Pageable pageable = PageRequest.of(0, size + 1);

        List<Post> posts;
        if (request.getCursor() == null) {
            // 첫 페이지
            posts = postRepository.findAllByOrderByCreatedAtDesc(pageable);
        } else {
            // 커서 이후 데이터
            posts = postRepository.findAllWithCursor(request.getCursor(), pageable);
        }

        boolean hasNext = posts.size() > size;
        if (hasNext) {
            posts = posts.subList(0, size);
        }

        LocalDateTime nextCursor = hasNext && !posts.isEmpty() ? posts.get(posts.size() - 1).getCreatedAt() : null;
        List<PostResponseDto.PostDto> postDtos = communityConverter.toPostDtoList(posts);

        return PostResponseDto.PostCursorResponse.builder()
                .posts(postDtos)
                .nextCursor(nextCursor)
                .hasNext(hasNext)
                .build();
    }

    /**
     * 지역별 게시글 조회 (커서 페이지네이션)
     */
    public PostResponseDto.PostCursorResponse getPostsByRegion(Long regionId, PostRequestDto.PostCursorRequest request) {
        Region region = regionRepository.findById(regionId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.RESOURCE_NOT_FOUND));

        int size = request.getSize() != null ? request.getSize() : 20;
        Pageable pageable = PageRequest.of(0, size + 1);

        List<Post> posts;
        if (request.getCursor() == null) {
            // 첫 페이지
            posts = postRepository.findAllByRegionOrderByCreatedAtDesc(region, pageable);
        } else {
            // 커서 이후 데이터
            posts = postRepository.findAllByRegionWithCursor(region, request.getCursor(), pageable);
        }

        boolean hasNext = posts.size() > size;
        if (hasNext) {
            posts = posts.subList(0, size);
        }

        LocalDateTime nextCursor = hasNext && !posts.isEmpty() ? posts.get(posts.size() - 1).getCreatedAt() : null;
        List<PostResponseDto.PostDto> postDtos = communityConverter.toPostDtoList(posts);

        return PostResponseDto.PostCursorResponse.builder()
                .posts(postDtos)
                .nextCursor(nextCursor)
                .hasNext(hasNext)
                .build();
    }

    /**
     * 태그별 게시글 조회 (커서 페이지네이션)
     */
    public PostResponseDto.PostCursorResponse getPostsByTag(String tagName, PostRequestDto.PostCursorRequest request) {
        int size = request.getSize() != null ? request.getSize() : 20;
        Pageable pageable = PageRequest.of(0, size + 1);

        List<Post> posts;
        if (request.getCursor() == null) {
            // 첫 페이지
            posts = postRepository.findAllByTagNameOrderByCreatedAtDesc(tagName, pageable);
        } else {
            // 커서 이후 데이터
            posts = postRepository.findAllByTagNameWithCursor(tagName, request.getCursor(), pageable);
        }

        boolean hasNext = posts.size() > size;
        if (hasNext) {
            posts = posts.subList(0, size);
        }

        LocalDateTime nextCursor = hasNext && !posts.isEmpty() ? posts.get(posts.size() - 1).getCreatedAt() : null;
        List<PostResponseDto.PostDto> postDtos = communityConverter.toPostDtoList(posts);

        return PostResponseDto.PostCursorResponse.builder()
                .posts(postDtos)
                .nextCursor(nextCursor)
                .hasNext(hasNext)
                .build();
    }

    /**
     * 게시글 검색 (제목 + 내용) - 커서 페이지네이션
     */
    public PostResponseDto.PostCursorResponse searchPosts(String keyword, PostRequestDto.PostCursorRequest request) {
        int size = request.getSize() != null ? request.getSize() : 20;
        Pageable pageable = PageRequest.of(0, size + 1);

        List<Post> posts;
        if (request.getCursor() == null) {
            // 첫 페이지
            posts = postRepository.searchPostsOrderByCreatedAtDesc(keyword, pageable);
        } else {
            // 커서 이후 데이터
            posts = postRepository.searchPostsWithCursor(keyword, request.getCursor(), pageable);
        }

        boolean hasNext = posts.size() > size;
        if (hasNext) {
            posts = posts.subList(0, size);
        }

        LocalDateTime nextCursor = hasNext && !posts.isEmpty() ? posts.get(posts.size() - 1).getCreatedAt() : null;
        List<PostResponseDto.PostDto> postDtos = communityConverter.toPostDtoList(posts);

        return PostResponseDto.PostCursorResponse.builder()
                .posts(postDtos)
                .nextCursor(nextCursor)
                .hasNext(hasNext)
                .build();
    }

    /**
     * 내가 쓴 글 조회
     */
    public List<PostResponseDto.PostDto> getMyPosts(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));

        List<Post> posts = postRepository.findAllByUser(user);
        return communityConverter.toPostDtoList(posts);
    }

    /**
     * 내가 댓글 단 글 조회
     */
    public List<PostResponseDto.PostDto> getPostsICommented(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));

        // 해당 사용자가 작성한 모든 댓글 조회
        List<Comment> comments = commentRepository.findAllByUser(user);

        // 댓글이 달린 게시글만 추출 (중복 제거)
        List<Post> posts = comments.stream()
                .map(Comment::getPost)
                .distinct()
                .collect(Collectors.toList());

        return communityConverter.toPostDtoList(posts);
    }
}