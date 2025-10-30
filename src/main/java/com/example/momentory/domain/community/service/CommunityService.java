package com.example.momentory.domain.community.service;

import com.example.momentory.domain.community.entity.Like;
import com.example.momentory.domain.community.entity.Post;
import com.example.momentory.domain.community.repository.LikeRepository;
import com.example.momentory.domain.community.repository.PostRepository;
// ... 다른 import들 유지 ...
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityNotFoundException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommunityService {

    // 1단계에서 생성한 Repository 의존성 주입
    private final LikeRepository likeRepository;
    private final PostRepository postRepository;

    @Transactional
    public boolean toggleLike(Long userId, Long postId) {

        // 1. Post 엔티티 조회
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("ID " + postId + "에 해당하는 게시글을 찾을 수 없습니다."));

        // 2. 기존 좋아요 레코드 존재 확인 (LikeRepository 사용)
        Optional<Like> existingLike = likeRepository.findByUserIdAndPost(userId, post);

        if (existingLike.isPresent()) {
            // 좋아요 취소 (DELETE)
            likeRepository.delete(existingLike.get());
            post.decreaseLikeCount(); // 2단계에서 정의한 메서드 호출
            return false; // 좋아요 취소됨

        } else {
            // 좋아요 생성 (INSERT)
            Like newLike = Like.builder()
                    .userId(userId)
                    .post(post)
                    .build();

            likeRepository.save(newLike);
            post.increaseLikeCount(); // 2단계에서 정의한 메서드 호출
            return true; // 좋아요 설정됨
        }
    }
}