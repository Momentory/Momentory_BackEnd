package com.example.momentory.domain.community.service;

import com.example.momentory.domain.community.converter.CommunityConverter;
import com.example.momentory.domain.community.dto.PostResponseDto;
import com.example.momentory.domain.community.entity.Like;
import com.example.momentory.domain.community.entity.Post;
import com.example.momentory.domain.community.repository.LikeRepository;
import com.example.momentory.domain.community.repository.PostRepository;
import com.example.momentory.domain.point.entity.PointActionType;
import com.example.momentory.domain.point.service.PointService;
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
    private final PointService pointService;
    private final CommunityConverter communityConverter;

    /**
     * 좋아요 토글 (설정/취소) - Soft Delete 방식
     * @param postId 게시글 ID
     * @return true: 좋아요 추가됨, false: 좋아요 취소됨
     */
    @Transactional
    public boolean toggleLike(Long postId) {
        User currentUser = userService.getCurrentUser();

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.POST_NOT_FOUND));

        // 기존 좋아요 찾기 (활성/비활성 무관)
        Optional<Like> existingLike = likeRepository.findByUserAndPost(currentUser, post);

        if (existingLike.isPresent()) {
            Like like = existingLike.get();

            if (like.isActive()) {
                // 활성화된 좋아요 -> 비활성화 (취소)
                like.deactivate();
                post.decreaseLikeCount();
                return false;
            } else {
                // 비활성화된 좋아요 -> 재활성화
                like.activate();
                post.increaseLikeCount();

                // ⚠️ 중요: 재활성화 시에는 포인트 지급 안 함 (중복 지급 방지)
                return true;
            }
        } else {
            // 새로운 좋아요 생성
            Like newLike = Like.builder()
                    .user(currentUser)
                    .post(post)
                    .isActive(true)
                    .build();

            likeRepository.save(newLike);
            post.increaseLikeCount();

            // 게시글 작성자에게 포인트 지급 (본인 게시글 제외, 일일 50회 제한)
            User postOwner = post.getUser();
            if (!postOwner.getUserId().equals(currentUser.getUserId())) {
                int likePoints = pointService.getPointAmount(PointActionType.RECEIVE_LIKE);
                pointService.addPoint(postOwner, likePoints, PointActionType.RECEIVE_LIKE);
            }

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