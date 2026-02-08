package com.example.auth.service;

import com.example.auth.dto.ImageUploadResponse;
import com.example.auth.dto.post.PostCreateRequest;
import com.example.auth.dto.post.PostListResponse;
import com.example.auth.dto.post.PostResponse;
import com.example.auth.dto.post.PostUpdateRequest;
import com.example.auth.entity.*;
import com.example.auth.exception.PostNotFoundException;
import com.example.auth.exception.UnauthorizedAccessException;
import com.example.auth.repository.PostImageRepository;
import com.example.auth.repository.PostRepository;
import com.example.auth.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostImageRepository postImageRepository;
    private final ImageStorageService imageStorageService;

    @Transactional
    public PostResponse createPost(Long userId, PostCreateRequest request) {
        return createPost(userId, request, null);
    }

    @Transactional
    public PostResponse createPost(Long userId, PostCreateRequest request, List<MultipartFile> images) {
        // 게시글 작성
        System.out.println("게시글 작성 시작");

        // DB로부터 사용자를 찾아서 User Entity 인스턴스로 반환
        User user = userRepository.findById(userId).orElseThrow(
                ()-> new UsernameNotFoundException("사용자를 찾을 수 없습니다.")
        );

        // 게시글 엔티티 인스턴스 생성
        Post post = new Post();
        post.setUser(user);
        post.setContent(request.getContent());
        post.setVisibility(request.getVisibility());

        // 게시글 저장
        Post savedPost = postRepository.save(post); // DB에 레코드 생성됨(저장)
        Long postId = savedPost.getId();

        // 이미지를 이미지 테이블에 저장하고 링킹
        List<PostImage> imageList = null;
        if (images != null && !images.isEmpty()) {
            imageList = savePostImages(savedPost, images);
        }

        // 게시글과 함께 저장된 이미지 링크들의 정보가 있는 Post Entity를 다시 불러와야 함
        savedPost = postRepository.findByIdWithUserAndImages(postId).orElseThrow(
                ()->new RuntimeException("게시글이 없습니다")
        );

        if (imageList!=null) {
            Post finalSavedPost = savedPost;
            for(PostImage postImage: imageList) {
                savedPost.addImage(postImage);
            }
        }

        System.out.println("가져온 게시글의 이미지 개수: " + savedPost.getImages().size());

        return Post.toDto(savedPost, false, false);
    }

    @Transactional
    private List<PostImage> savePostImages(Post post, List<MultipartFile> images) {
        List<PostImage> postImages = new ArrayList<>();

        for(int i=0; i<images.size(); i++) {
            MultipartFile file = images.get(i);

            // 이미지 업로드 디렉토리에 저장하고 response를 받음
            ImageUploadResponse uploadResponse = imageStorageService.store(file);

            PostImage postImage = new PostImage();
            postImage.setPost(post);    // 게시글과 링킹
            postImage.setImageUrl(uploadResponse.getImageUrl());
            postImage.setThumbnailUrl(uploadResponse.getImageUrl());    // 나중에 구현하기로...
            postImage.setSortOrder(i);
            postImage.setFileSize(uploadResponse.getFileSize().intValue());
            postImage.setMediaType(MediaType.IMAGE);    // IMAGE Type만 현재는 지원함..(나중에 알아서 구현)

            postImages.add( postImage );
        }

        // 이미지 일괄 저장
        return postImageRepository.saveAll(postImages);
    }

    @Transactional
    public PostResponse updatePost(Long userId, Long postId, @Valid PostUpdateRequest request) {
        // postId에 해당하는 게시글을 우선 가져온다.
        Post post = postRepository.findByIdAndIsDeletedFalse(postId)
            .orElseThrow(()-> new PostNotFoundException(postId)
        );

        // 작성자 확인
        if (!post.getUser().getId().equals(userId)) {
            throw new UnauthorizedAccessException("게시글을 수정할 권한이 없습니다.");
        }

        // 기존 Post글에 새로운 내용으로 업데이트
        if (request.getContent() != null) {
            post.setContent(request.getContent());
        }

        System.out.println(request.getVisibility());
        if (request.getVisibility()!=null) {
            post.setVisibility(request.getVisibility());
        }

        post = postRepository.save(post);

        // 로그
        log.info("게시글 수정 완료 - postId: {}", postId);

        return Post.toDto(post, post.getLikeCount()!=0, false);
    }

    @Transactional
    public void deletePost(Long userId, Long postId) {
        Post post = postRepository.findByIdAndIsDeletedFalse(postId)
                .orElseThrow(()-> new PostNotFoundException(postId));

        // 작성자 확인
        if (!post.getUser().getId().equals(userId)) {
            throw new UnauthorizedAccessException("게시글을 수정할 권한이 없습니다.");
        }

        post.softDelete();
        postRepository.save(post);

        log.info("게시글 삭제 완료 - postId: {}", postId);
    }

    // 공개 게시글 목록 조회
    @Transactional(readOnly = true)
    public Page<PostListResponse> getPublicPosts(Pageable pageable) {
        Page<Post> posts = postRepository.findByVisibilityAndIsDeletedFalse(
                Visibility.PUBLIC, pageable);

        return posts.map(Post::toListDto);
    }

    @Transactional(readOnly = true)
    public Page<PostListResponse> getMyPosts(Long userId, Pageable pageable) {

        Page<Post> posts = postRepository.findByUserIdAndIsDeletedFalseOrderByCreatedAtDesc(userId, pageable);
        return posts.map(Post::toListDto);
    }

    public PostResponse getPost(Long userId, Long postId) {
        // postId로 게시글 조회
        Post post = postRepository.findByIdWithUserAndImages(postId)
                .orElseThrow(()->new PostNotFoundException(postId));

        //
        if (!canViewPost(userId, post)) {
            // 볼 권한 없음
            throw new UnauthorizedAccessException("이 게시글을 볼 권한이 없습니다.");
        }

        // 자기 자신이 본인 글 보는 경우가 아니라면 ===> 조회수 증가시키기
        if (!post.getUser().getId().equals(userId)) {
            postRepository.incrementViewCount(post.getId());
        }

        return Post.toDto(post, post.getLikeCount()!=0, false);
    }

    private boolean canViewPost(Long userId, Post post) {
        if (post.getUser().getId().equals(userId)) {
            // 자기 자신임
            return true;
        }

        switch (post.getVisibility()) {
            case PUBLIC: return true;
            case PRIVATE: return false;
            case FOLLOWERS:
                // userId가 Follower인지 확인
                return true;
            default: return false;
        }
    }
}
