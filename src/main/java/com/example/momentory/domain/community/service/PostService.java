package com.example.momentory.domain.community.service;

import com.example.momentory.domain.community.converter.CommunityConverter;
import com.example.momentory.domain.community.dto.PostRequestDto;
import com.example.momentory.domain.community.dto.PostResponseDto;
import com.example.momentory.domain.community.entity.Post;
import com.example.momentory.domain.community.repository.PostRepository;
import com.example.momentory.domain.map.entity.Region;
import com.example.momentory.domain.map.repository.RegionRepository;
import com.example.momentory.domain.tag.entity.PostTag;
import com.example.momentory.domain.tag.entity.Tag;
import com.example.momentory.domain.tag.entity.TagType;
import com.example.momentory.domain.tag.repository.PostTagRepository;
import com.example.momentory.domain.tag.repository.TagRepository;
import com.example.momentory.domain.user.entity.User;
import com.example.momentory.domain.user.repository.UserRepository;
import com.example.momentory.global.code.status.ErrorStatus;
import com.example.momentory.global.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final RegionRepository regionRepository;
    private final TagRepository tagRepository;
    private final PostTagRepository postTagRepository;
    private final CommunityConverter communityConverter;

    /**
     * 게시글 생성
     */
    @Transactional
    public PostResponseDto.PostSimpleDto createPost(Long userId, PostRequestDto.CreatePostDto request) {
        // User 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));

        // Region 조회 (선택사항)
        Region region = null;
        if (request.getRegionId() != null) {
            region = regionRepository.findById(request.getRegionId())
                    .orElseThrow(() -> new GeneralException(ErrorStatus.RESOURCE_NOT_FOUND));
        }

        // Post 생성
        Post post = Post.builder()
                .user(user)
                .title(request.getTitle())
                .content(request.getContent())
                .imageUrl(request.getImageUrl())
                .imageName(request.getImageName())
                .region(region)
                .build();

        Post savedPost = postRepository.save(post);

        // 태그 처리 (선택사항)
        if (request.getTags() != null && !request.getTags().isEmpty()) {
            for (String tagName : request.getTags()) {
                Tag tag = tagRepository.findByNameAndType(tagName, TagType.POST)
                        .orElseGet(() -> tagRepository.save(Tag.builder()
                                .name(tagName)
                                .type(TagType.POST)
                                .build()));

                PostTag postTag = PostTag.builder()
                        .post(savedPost)
                        .tag(tag)
                        .build();
                postTagRepository.save(postTag);
            }
        }

        return communityConverter.toPostSimpleDto(savedPost);
    }

    /**
     * 게시글 수정
     */
    @Transactional
    public PostResponseDto.PostSimpleDto updatePost(Long postId, Long userId, PostRequestDto.UpdatePostDto request) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.POST_NOT_FOUND));

        // 작성자 확인
        if (!post.getUser().getId().equals(userId)) {
            throw new GeneralException(ErrorStatus._FORBIDDEN);
        }

        // Region 조회 (변경하는 경우)
        Region region = null;
        if (request.getRegionId() != null) {
            region = regionRepository.findById(request.getRegionId())
                    .orElseThrow(() -> new GeneralException(ErrorStatus.RESOURCE_NOT_FOUND));
        }

        // Post 업데이트
        post.updatePost(request.getTitle(), request.getContent(), region,
                request.getImageUrl(), request.getImageName());

        // 태그 업데이트 (기존 태그 삭제 후 재생성)
        if (request.getTags() != null) {
            postTagRepository.deleteAllByPost(post);

            for (String tagName : request.getTags()) {
                Tag tag = tagRepository.findByNameAndType(tagName, TagType.POST)
                        .orElseGet(() -> tagRepository.save(Tag.builder()
                                .name(tagName)
                                .type(TagType.POST)
                                .build()));

                PostTag postTag = PostTag.builder()
                        .post(post)
                        .tag(tag)
                        .build();
                postTagRepository.save(postTag);
            }
        }

        return communityConverter.toPostSimpleDto(post);
    }

    /**
     * 게시글 삭제
     */
    @Transactional
    public void deletePost(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.POST_NOT_FOUND));

        // 작성자 확인
        if (!post.getUser().getId().equals(userId)) {
            throw new GeneralException(ErrorStatus._FORBIDDEN);
        }

        postRepository.delete(post);
    }
}
