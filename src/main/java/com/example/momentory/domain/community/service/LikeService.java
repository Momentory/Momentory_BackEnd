package com.example.momentory.domain.community.service;

import com.example.momentory.domain.community.converter.CommunityConverter;
import com.example.momentory.domain.community.dto.PostResponseDto;
import com.example.momentory.domain.community.entity.Like;
import com.example.momentory.domain.community.entity.Post;
import com.example.momentory.domain.community.repository.LikeRepository;
import com.example.momentory.domain.community.repository.PostRepository;
import com.example.momentory.domain.user.entity.User;
import com.example.momentory.domain.user.service.UserService;
import com.example.momentory.global.code.status.ErrorStatus;
import com.example.momentory.global.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final UserService userService;
    private final CommunityConverter communityConverter;

    /**
     * 좋아요 토글 (설정/취소)
     */
    @Transactional
    public boolean toggleLike(Long postId) {
        User user = userService.getCurrentUser();

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.POST_NOT_FOUND));

        Optional<Like> existingLike = likeRepository.findByUserAndPost(user, post);

        if (existingLike.isPresent()) {
            likeRepository.delete(existingLike.get());
            post.decreaseLikeCount();
            return false;
        } else {
            Like newLike = Like.builder()
                    .user(user)
                    .post(post)
                    .build();

            likeRepository.save(newLike);
            post.increaseLikeCount();
            return true;
        }
    }

    /**
     * 사용자가 좋아요한 게시글 목록 조회 (postId와 imageUrl만)
     */
    @Transactional(readOnly = true)
    public List<PostResponseDto.PostThumbnailDto> getUserLikedPosts() {
        User user = userService.getCurrentUser();

        // DB에서 직접 postId와 imageUrl만 조회
        return likeRepository.findPostThumbnailsByUser(user);
    }
}