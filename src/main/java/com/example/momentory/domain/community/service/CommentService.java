package com.example.momentory.domain.community.service;

import com.example.momentory.domain.community.converter.CommunityConverter;
import com.example.momentory.domain.community.dto.CommentRequestDto;
import com.example.momentory.domain.community.dto.CommentResponseDto;
import com.example.momentory.domain.community.entity.Comment;
import com.example.momentory.domain.community.entity.Post;
import com.example.momentory.domain.community.repository.CommentRepository;
import com.example.momentory.domain.community.repository.PostRepository;
import com.example.momentory.domain.notification.entity.NotificationType;
import com.example.momentory.domain.notification.event.NotificationEvent;
import com.example.momentory.domain.user.entity.User;
import com.example.momentory.domain.user.repository.UserRepository;
import com.example.momentory.global.code.status.ErrorStatus;
import com.example.momentory.global.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CommunityConverter communityConverter;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * 댓글 생성
     */
    @Transactional
    public Comment createComment(Long userId, Long postId, CommentRequestDto.CreateCommentDto request) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.POST_NOT_FOUND));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));

        Comment comment = Comment.builder()
                .content(request.getContent())
                .user(user)
                .post(post)
                .build();

        Comment savedComment = commentRepository.save(comment);

        // 댓글 개수 증가
        post.increaseCommentCount();

        // 게시글 작성자에게 알림 발송 (본인 글에 본인이 댓글 단 경우 제외)
        if (!post.getUser().getId().equals(userId)) {
            NotificationEvent event = NotificationEvent.builder()
                    .targetUser(post.getUser())
                    .type(NotificationType.COMMENT)
                    .message(user.getNickname() + "님이 회원님의 게시글에 댓글을 남겼습니다.")
                    .relatedId(post.getPostId())
                    .build();
            eventPublisher.publishEvent(event);
        }

        return savedComment;
    }

    /**
     * 댓글 목록 조회 (커서 페이지네이션)
     */
    @Transactional(readOnly = true)
    public CommentResponseDto.CommentCursorResponse getComments(Long postId, CommentRequestDto.CommentCursorRequest request) {
        int size = request.getSize() != null ? request.getSize() : 20;
        Pageable pageable = PageRequest.of(0, size + 1);

        List<Comment> comments;
        if (request.getCursor() == null) {
            // 첫 페이지
            comments = commentRepository.findAllByPostPostIdOrderByCreatedAtDesc(postId, pageable);
        } else {
            // 커서 이후 데이터
            comments = commentRepository.findAllByPostPostIdWithCursor(postId, request.getCursor(), pageable);
        }

        boolean hasNext = comments.size() > size;
        if (hasNext) {
            comments = comments.subList(0, size);
        }

        LocalDateTime nextCursor = hasNext && !comments.isEmpty() ? comments.get(comments.size() - 1).getCreatedAt() : null;
        List<CommentResponseDto.CommentDto> commentDtos = communityConverter.toCommentDtoList(comments);

        return CommentResponseDto.CommentCursorResponse.builder()
                .comments(commentDtos)
                .nextCursor(nextCursor)
                .hasNext(hasNext)
                .build();
    }

    /**
     * 댓글 수정
     */
    @Transactional
    public Comment updateComment(Long commentId, Long userId, CommentRequestDto.UpdateCommentDto request) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.COMMENT_NOT_FOUND));

        if (!comment.getUser().getId().equals(userId)) {
            throw new GeneralException(ErrorStatus.COMMENT_UPDATE_FORBIDDEN);
        }

        comment.updateContent(request.getContent());
        return comment;
    }

    /**
     * 댓글 삭제
     */
    @Transactional
    public void deleteComment(Long commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.COMMENT_NOT_FOUND));

        if (!comment.getUser().getId().equals(userId)) {
            throw new GeneralException(ErrorStatus.COMMENT_DELETE_FORBIDDEN);
        }

        Post post = comment.getPost();
        commentRepository.delete(comment);

        // 댓글 개수 감소
        post.decreaseCommentCount();
    }
}