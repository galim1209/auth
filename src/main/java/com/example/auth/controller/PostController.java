package com.example.auth.controller;

import com.example.auth.dto.ApiResponse;
import com.example.auth.dto.post.PostCreateRequest;
import com.example.auth.dto.post.PostListResponse;
import com.example.auth.dto.post.PostResponse;
import com.example.auth.dto.post.PostUpdateRequest;
import com.example.auth.entity.Post;
import com.example.auth.entity.User;
import com.example.auth.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;


    /**
     * 게시글 작성(이미지 미포함)
     * POST /api/posts
     * Content-Type: application/json
     * */
    @PostMapping
    public ResponseEntity<ApiResponse<PostResponse>> createPost(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody PostCreateRequest request
            ) {
        log.info("게시글 작성 요청: userId({})", user.getId());

        PostResponse response = postService.createPost(user.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("게시글이 작성되었습니다", response));
    }


    /**
     * 게시글 작성(이미지 포함)
     * POST /api/posts/with-images
     * Content-Type: multipart/form-data
     *
     * 요청 파라미터
     * - post: json형식의 게시글 정보(PostCreateRequest)
     * - images: 이미지 파일들
     * */
    @PostMapping(value = "/with-images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<PostResponse>> createPostWithImages(
            @AuthenticationPrincipal User user,
            @Valid @RequestPart("post") PostCreateRequest request,
            @RequestPart(value = "images", required = false) List<MultipartFile> images
    ) {
        log.info("게시글 작성 요청(이미지 포함): userId({}), 이미지 개수({})", user.getId(), images.size());

        PostResponse response = postService.createPost(user.getId(), request, images);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("게시글이 작성되었습니다", response));
    }

    /**
     * 게시글 수정(이미지 포함)
     * PUT /api/posts/{id}
     * Content-Type: multipart/form-data
     *
     * 요청 파라미터
     * - post: json형식의 게시글 정보(PostCreateRequest)
     * - images: 이미지 파일들
     * */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PostResponse>> updatePost(
            @AuthenticationPrincipal User user,
            @PathVariable Long id,  // 게시글 id
            @Valid @RequestBody PostUpdateRequest request
    ) {
        log.info("게시글 수정 요청 = userId: {}, postId: {}", user.getId(), id);

        PostResponse response = postService.updatePost(user.getId(), id, request);

        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.success("게시글 수정 완료", response)
        );
    }


    /**
     * 게시글 삭제
     * DELETE /api/posts/{id}
     *
     * 요청 파라미터
     * - 인증된 사용자
     * - 게시글 ID
     * */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePost(
            @AuthenticationPrincipal User user,
            @PathVariable Long id
    ) {
        log.info("게시글 삭제 요청 = userId: {}, postId: {}", user.getId(), id);
        postService.deletePost(user.getId(), id);

        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.success("게시글 삭제 완료", null)
        );
    }


    /**
     * 공개(PUBLIC)게시글 조회
     * GET /api/posts
     *
     * 요청 파라미터
     * - Page 시작정보와 Page 사이즈
     * */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<PostListResponse>>> getPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        if (size > 50) size = 50;

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<PostListResponse> posts = postService.getPublicPosts(pageable);

        return ResponseEntity.ok(ApiResponse.success("게시글 목록 조회 성공", posts));
    }

    /**
     * 내 게시글 목록 조회
     * GET /api/posts/me
     *
     * 요청 파라미터
     * - 인증된 사용자
     * - Page 시작정보
     * - Page 사이즈
     * */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<Page<PostListResponse>>> getMyPosts(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        log.info("내 게시글 목록 조회 요청 = userId: {}", user.getId());

        if (size > 50) size = 50;

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<PostListResponse> posts = postService.getMyPosts(user.getId(), pageable);

        return ResponseEntity.ok(ApiResponse.success("내 게시글 목록 조회 성공", posts));
    }


    // A사용자가 B사용자의 게시글 목록 조회 요청
    // getUserPosts
    // /api/posts/user/{userId}
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<Page<PostListResponse>>> getUserPosts(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        log.info("특정 사용자 게시글 목록 조회 요청 = userId: {}", userId);

        if (size > 50) size = 50;

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<PostListResponse> posts = postService.getMyPosts(userId, pageable);

        return ResponseEntity.ok(ApiResponse.success("사용자 게시글 목록 조회 성공", posts));
    }

    /**
     * 내 게시글 상세 조회
     * GET /api/posts/{id}
     *
     * 요청 파라미터
     * - 인증된 사용자
     * - 게시글 id
     * */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PostResponse>> getPost(
            @AuthenticationPrincipal User user,
            @PathVariable Long id
    ) {
        log.info("내 게시글 상세 조회 요청 = userId: {}, postId:{}", user.getId(), id);

        PostResponse response = postService.getPost(user.getId(), id);
        return ResponseEntity.ok(ApiResponse.success("게시글 조회 성공", response));
    }
}
