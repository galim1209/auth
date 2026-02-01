package com.example.auth.controller;

import com.example.auth.dto.ApiResponse;
import com.example.auth.dto.post.PostCreateRequest;
import com.example.auth.dto.post.PostResponse;
import com.example.auth.entity.Post;
import com.example.auth.entity.User;
import com.example.auth.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
     * 게시글 작성
     * POST /api/posts
     * Content-Type: application/json
     * */
    @PostMapping
    public ResponseEntity<ApiResponse<PostResponse>> createPost(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody PostCreateRequest request
            ){
        log.info("게시글 작성 요청: userId({})", user.getId());

        PostResponse response = postService.createPost(user.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("게시글이 작성되었습니다", response));
    }

    /**
     * 게시글 작성
     * POST /api/posts/with-images
     * Content-Type: multipart/from-data
     *
     * 요청 파라미터
     * - post: json형식의 게시글 정보 ( PostCreateRequest )
     * - images: 이미지 파일들
     * */
    @PostMapping(value = "/with-images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<PostResponse>> createPostWithImages(
            @AuthenticationPrincipal User user,
            @Valid @RequestPart("post") PostCreateRequest request,
            @RequestPart(value = "images", required = false) List<MultipartFile> images
    ){
        log.info("게시글 작성 요청(이미지 포함): userId({}), 이미지 개수({})",user.getId(),images.size());

        PostResponse response = postService.createPost(user.getId(),request,images);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("게시글이 작성되었습니다", response));

    }


}
