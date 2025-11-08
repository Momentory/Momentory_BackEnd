package com.example.momentory.domain.community.converter;

import com.example.momentory.domain.community.entity.Comment;
import com.example.momentory.domain.community.entity.Post;
import com.example.momentory.domain.community.dto.CommentResponseDto;
import com.example.momentory.domain.community.dto.PostResponseDto;
import com.example.momentory.domain.community.repository.ScrapRepository;
import com.example.momentory.domain.user.entity.User;
import com.example.momentory.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CommunityConverter {

    private final ScrapRepository scrapRepository;
    private final UserService userService;

    /**
     * Comment 엔티티를 CommentDto로 변환
     */
    public CommentResponseDto.CommentDto toCommentDto(Comment comment) {
        return CommentResponseDto.CommentDto.builder()
                .commentId(comment.getCommentId())
                .userId(comment.getUser().getId())
                .userNickname(comment.getUser().getNickname())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }

    /**
     * Post 엔티티를 PostDto로 변환 (상세 정보 포함)
     */
    public PostResponseDto.PostDto toPostDto(Post post) {
        List<String> tags = post.getPostTags().stream()
                .map(postTag -> postTag.getTag().getName())
                .collect(Collectors.toList());

        String userProfileImageUrl = null;
        String userProfileImageName = null;
        if (post.getUser().getProfile() != null) {
            userProfileImageUrl = post.getUser().getProfile().getImageUrl();
            userProfileImageName = post.getUser().getProfile().getImageName();
        }

        // 현재 사용자의 스크랩 여부 확인
        User currentUser = userService.getCurrentUser();
        boolean isScrapped = scrapRepository.findByUserAndPost(currentUser, post).isPresent();

        return PostResponseDto.PostDto.builder()
                .postId(post.getPostId())
                .userId(post.getUser().getId())
                .userNickname(post.getUser().getNickname())
                .userProfileImageUrl(userProfileImageUrl)
                .userProfileImageName(userProfileImageName)
                .title(post.getTitle())
                .content(post.getContent())
                .imageUrl(post.getImageUrl())
                .imageName(post.getImageName())
                .regionId(post.getRegion() != null ? post.getRegion().getId() : null)
                .regionName(post.getRegion() != null ? post.getRegion().getName() : null)
                .tags(tags)
                .likeCount(post.getLikeCount())
                .commentCount(post.getCommentCount())
                .isScrapped(isScrapped)
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();
    }

    /**
     * Post 엔티티를 PostSimpleDto로 변환 (ID와 제목만)
     */
    public PostResponseDto.PostSimpleDto toPostSimpleDto(Post post) {
        return PostResponseDto.PostSimpleDto.builder()
                .postId(post.getPostId())
                .title(post.getTitle())
                .build();
    }

    /**
     * Post 엔티티 리스트를 PostDto 리스트로 변환
     */
    public List<PostResponseDto.PostDto> toPostDtoList(List<Post> posts) {
        return posts.stream()
                .map(this::toPostDto)
                .collect(Collectors.toList());
    }

    /**
     * Comment 엔티티 리스트를 CommentDto 리스트로 변환
     */
    public List<CommentResponseDto.CommentDto> toCommentDtoList(List<Comment> comments) {
        return comments.stream()
                .map(this::toCommentDto)
                .collect(Collectors.toList());
    }

    /**
     * Post 엔티티를 PostThumbnailDto로 변환 (postId와 imageUrl만)
     */
    public PostResponseDto.PostThumbnailDto toPostThumbnailDto(Post post) {
        return PostResponseDto.PostThumbnailDto.builder()
                .postId(post.getPostId())
                .imageUrl(post.getImageUrl())
                .build();
    }

    /**
     * Post 엔티티 리스트를 PostThumbnailDto 리스트로 변환
     */
    public List<PostResponseDto.PostThumbnailDto> toPostThumbnailDtoList(List<Post> posts) {
        return posts.stream()
                .map(this::toPostThumbnailDto)
                .collect(Collectors.toList());
    }
}