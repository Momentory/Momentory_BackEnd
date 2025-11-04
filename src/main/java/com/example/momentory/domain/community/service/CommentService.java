package com.example.momentory.domain.community.service;

import com.example.momentory.domain.community.converter.CommunityConverter;
import com.example.momentory.domain.community.dto.CommentRequestDto;
import com.example.momentory.domain.community.dto.CommentResponseDto;
import com.example.momentory.domain.community.entity.Comment;
import com.example.momentory.domain.community.entity.Post;
import com.example.momentory.domain.community.repository.CommentRepository;
import com.example.momentory.domain.community.repository.PostRepository;
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

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CommunityConverter communityConverter;

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

        return commentRepository.save(comment);
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

        commentRepository.delete(comment);
    }
}