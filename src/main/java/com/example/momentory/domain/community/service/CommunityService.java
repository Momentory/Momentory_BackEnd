package com.example.momentory.domain.community.service;

import com.example.momentory.domain.community.entity.Like;
import com.example.momentory.domain.community.entity.Post;
import com.example.momentory.domain.community.entity.Comment; // 🚨 Comment 엔티티 추가
import com.example.momentory.domain.community.repository.LikeRepository;
import com.example.momentory.domain.community.repository.PostRepository;
import com.example.momentory.domain.community.repository.CommentRepository; // 🚨 CommentRepository 추가
import com.example.momentory.domain.community.dto.CommentRequestDto; // 🚨 CommentRequestDto 추가
import com.example.momentory.domain.community.dto.CommentResponseDto; // 🚨 CommentResponseDto 추가
import com.example.momentory.domain.user.entity.User; // 🚨 User 엔티티 추가
import com.example.momentory.domain.user.repository.UserRepository; // 🚨 UserRepository 추가

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityNotFoundException;
import java.util.Optional;
import java.util.List;
import java.util.stream.Collectors; // List 처리를 위해 추가

@Service
@RequiredArgsConstructor
public class CommunityService {

    // --- 기존 의존성 주입 ---
    private final LikeRepository likeRepository;
    private final PostRepository postRepository;

    // --- 🚨 댓글 CRUD를 위한 의존성 주입 ---
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;


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
    // 🚨🚨 댓글 (Comment) CRUD 로직 추가 🚨🚨
    // ----------------------------------------------------------------------

    /**
     * 1. 댓글 생성 (Create)
     */
    @Transactional
    public Comment createComment(Long userId, Long postId, CommentRequestDto.CreateCommentDto request) {

        // 1. Post 엔티티 조회 (게시글 존재 확인)
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("ID " + postId + "에 해당하는 게시글을 찾을 수 없습니다."));

        // 2. User 엔티티 조회 (작성자 확인)
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

        // 댓글 리스트를 DTO 리스트로 변환
        return comments.stream()
                .map(comment -> CommentResponseDto.CommentDto.builder()
                        .commentId(comment.getCommentId()) // Comment 엔티티의 ID 필드 이름이 commentId라고 가정
                        .userId(comment.getUser().getId())
                        .userNickname(comment.getUser().getNickname()) // User 엔티티의 닉네임 필드를 가져와야 함
                        .content(comment.getContent())
                        .createdAt(comment.getCreatedAt())
                        .updatedAt(comment.getUpdatedAt()) // BaseEntity의 ModifiedAt 필드를 사용한다고 가정
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

        // 🚨 작성자 검증: 요청한 사용자가 댓글 작성자인지 확인
        if (!comment.getUser().getId().equals(userId)) {
            // throw new CustomException(ErrorCode.FORBIDDEN_ACCESS, "댓글 수정 권한이 없습니다."); // 프로젝트의 권한 예외를 사용하세요.
            throw new RuntimeException("댓글 수정 권한이 없습니다."); // 임시 RuntimeException 사용
        }

        comment.updateContent(request.getContent()); // Comment 엔티티의 수정 메서드 호출
        return comment;
    }

    /**
     * 4. 댓글 삭제 (Delete)
     */
    @Transactional
    public void deleteComment(Long commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("ID " + commentId + "에 해당하는 댓글을 찾을 수 없습니다."));

        // 🚨 작성자 검증: 요청한 사용자가 댓글 작성자인지 확인
        if (!comment.getUser().getId().equals(userId)) {
            // throw new CustomException(ErrorCode.FORBIDDEN_ACCESS, "댓글 삭제 권한이 없습니다."); // 프로젝트의 권한 예외를 사용하세요.
            throw new RuntimeException("댓글 삭제 권한이 없습니다."); // 임시 RuntimeException 사용
        }

        commentRepository.delete(comment);
    }
}