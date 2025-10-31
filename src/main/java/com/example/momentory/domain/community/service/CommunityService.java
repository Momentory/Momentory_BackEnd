package com.example.momentory.domain.community.service;

import com.example.momentory.domain.community.entity.Like;
import com.example.momentory.domain.community.entity.Post;
import com.example.momentory.domain.community.entity.Comment;
import com.example.momentory.domain.community.entity.Scrap; // 🚨 Scrap 엔티티 추가
import com.example.momentory.domain.community.repository.LikeRepository;
import com.example.momentory.domain.community.repository.PostRepository;
import com.example.momentory.domain.community.repository.CommentRepository;
import com.example.momentory.domain.community.repository.ScrapRepository; // 🚨 ScrapRepository 추가
import com.example.momentory.domain.community.dto.CommentRequestDto;
import com.example.momentory.domain.community.dto.CommentResponseDto;
import com.example.momentory.domain.user.entity.User;
import com.example.momentory.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityNotFoundException;
import java.util.Optional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommunityService {

    // --- 기존 의존성 주입 ---
    private final LikeRepository likeRepository;
    private final PostRepository postRepository;

    // --- 🚨 댓글 CRUD 및 스크랩을 위한 의존성 주입 ---
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final ScrapRepository scrapRepository; // 🚨 스크랩 리포지토리 추가


    // --- 기존 좋아요 토글 로직 유지 ---
    @Transactional
    public boolean toggleLike(Long userId, Long postId) {
        // ... (기존 toggleLike 메서드 코드) ...
        // 생략합니다.
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("ID " + postId + "에 해당하는 게시글을 찾을 수 없습니다."));

        Optional<Like> existingLike = likeRepository.findByUserIdAndPost(userId, post);

        if (existingLike.isPresent()) {
            likeRepository.delete(existingLike.get());
            post.decreaseLikeCount();
            return false;
        } else {
            Like newLike = Like.builder()
                    .userId(userId)
                    .post(post)
                    .build();

            likeRepository.save(newLike);
            post.increaseLikeCount();
            return true;
        }
    }


    // ----------------------------------------------------------------------
    // 🚨🚨 댓글 (Comment) CRUD 로직 유지 🚨🚨
    // ----------------------------------------------------------------------

    /**
     * 1. 댓글 생성 (Create)
     */
    @Transactional
    public Comment createComment(Long userId, Long postId, CommentRequestDto.CreateCommentDto request) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("ID " + postId + "에 해당하는 게시글을 찾을 수 없습니다."));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("ID " + userId + "에 해당하는 사용자를 찾을 수 없습니다."));

        Comment comment = Comment.builder()
                .content(request.getContent())
                .user(user)
                .post(post)
                .build();

        return commentRepository.save(comment);
    }

    /**
     * 2. 댓글 목록 조회 (Read)
     */
    @Transactional(readOnly = true)
    public List<CommentResponseDto.CommentDto> getComments(Long postId) {

        List<Comment> comments = commentRepository.findAllByPostPostId(postId);

        return comments.stream()
                .map(comment -> CommentResponseDto.CommentDto.builder()
                        .commentId(comment.getCommentId())
                        .userId(comment.getUser().getId())
                        .userNickname(comment.getUser().getNickname())
                        .content(comment.getContent())
                        .createdAt(comment.getCreatedAt())
                        .updatedAt(comment.getUpdatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * 3. 댓글 수정 (Update)
     */
    @Transactional
    public Comment updateComment(Long commentId, Long userId, CommentRequestDto.UpdateCommentDto request) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("ID " + commentId + "에 해당하는 댓글을 찾을 수 없습니다."));

        if (!comment.getUser().getId().equals(userId)) {
            throw new RuntimeException("댓글 수정 권한이 없습니다.");
        }

        comment.updateContent(request.getContent());
        return comment;
    }

    /**
     * 4. 댓글 삭제 (Delete)
     */
    @Transactional
    public void deleteComment(Long commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("ID " + commentId + "에 해당하는 댓글을 찾을 수 없습니다."));

        if (!comment.getUser().getId().equals(userId)) {
            throw new RuntimeException("댓글 삭제 권한이 없습니다.");
        }

        commentRepository.delete(comment);
    }

    // ----------------------------------------------------------------------
    // 🚨🚨 스크랩 (Scrap) 기능 로직 추가 🚨🚨
    // ----------------------------------------------------------------------

    /**
     * 5. 스크랩 토글 (설정/취소)
     */
    @Transactional
    public boolean toggleScrap(Long userId, Long postId) {

        // 1. Post 엔티티 조회
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("ID " + postId + "에 해당하는 게시글을 찾을 수 없습니다."));

        // 🚨 2. User 엔티티 조회 (Scrap 엔티티 생성 및 조회를 위해 필수)
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("ID " + userId + "에 해당하는 사용자를 찾을 수 없습니다."));

        // 3. 기존 스크랩 레코드 존재 확인 (findByUserAndPost 사용)
        Optional<Scrap> existingScrap = scrapRepository.findByUserAndPost(user, post);

        if (existingScrap.isPresent()) {
            // 스크랩 취소 (DELETE)
            scrapRepository.delete(existingScrap.get());
            return false;
        } else {
            // 스크랩 생성 (INSERT)
            Scrap newScrap = Scrap.builder()
                    .user(user) // User 엔티티 주입
                    .post(post)
                    .build();

            scrapRepository.save(newScrap);
            return true;
        }
    }

    /**
     * 6. 사용자별 스크랩 목록 조회
     */
    @Transactional(readOnly = true)
    public List<Post> getUserScrapList(Long userId) {

        // 1. User 엔티티 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("ID " + userId + "에 해당하는 사용자를 찾을 수 없습니다."));

        // 2. 해당 사용자의 모든 Scrap 엔티티 조회 (findAllByUser 사용)
        List<Scrap> scrapList = scrapRepository.findAllByUser(user);

        // 3. Post 엔티티만 추출하여 반환
        return scrapList.stream()
                .map(Scrap::getPost)
                .collect(Collectors.toList());
    }
}