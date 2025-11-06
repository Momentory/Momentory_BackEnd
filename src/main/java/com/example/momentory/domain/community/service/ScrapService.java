package com.example.momentory.domain.community.service;

import com.example.momentory.domain.community.converter.CommunityConverter;
import com.example.momentory.domain.community.dto.PostResponseDto;
import com.example.momentory.domain.community.entity.Post;
import com.example.momentory.domain.community.entity.Scrap;
import com.example.momentory.domain.community.repository.PostRepository;
import com.example.momentory.domain.community.repository.ScrapRepository;
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
public class ScrapService {

    private final ScrapRepository scrapRepository;
    private final PostRepository postRepository;
    private final UserService userService;
    private final CommunityConverter communityConverter;

    /**
     * 스크랩 토글 (설정/취소)
     */
    @Transactional
    public boolean toggleScrap(Long postId) {
        User user = userService.getCurrentUser();

        // Post 엔티티 조회
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.POST_NOT_FOUND));

        // 기존 스크랩 레코드 존재 확인
        Optional<Scrap> existingScrap = scrapRepository.findByUserAndPost(user, post);

        if (existingScrap.isPresent()) {
            // 스크랩 취소
            scrapRepository.delete(existingScrap.get());
            return false;
        } else {
            // 스크랩 생성
            Scrap newScrap = Scrap.builder()
                    .user(user)
                    .post(post)
                    .build();

            scrapRepository.save(newScrap);
            return true;
        }
    }

    /**
     * 사용자별 스크랩 목록 조회 (postId와 imageUrl만)
     */
    @Transactional(readOnly = true)
    public List<PostResponseDto.PostThumbnailDto> getUserScrapList() {
        User user = userService.getCurrentUser();

        // 해당 사용자의 모든 Scrap 엔티티 조회
        List<Scrap> scrapList = scrapRepository.findAllByUser(user);

        // Post 엔티티만 추출하여 DTO로 변환
        List<Post> posts = scrapList.stream()
                .map(Scrap::getPost)
                .collect(Collectors.toList());

        return communityConverter.toPostThumbnailDtoList(posts);
    }
}