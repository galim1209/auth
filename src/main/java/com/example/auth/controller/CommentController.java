package com.example.auth.controller;

import com.example.auth.dto.ApiResponse;
import com.example.auth.dto.comment.CommentCreateRequest;
import com.example.auth.dto.comment.CommentResponse;
import com.example.auth.entity.Comment;
import com.example.auth.entity.User;
import com.example.auth.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @PostMapping("/api/posts/{postId}/comment")
    public ResponseEntity<ApiResponse<CommentResponse>> createComment(
            @AuthenticationPrincipal User user,
            @PathVariable Long postId,
            @Valid @RequestBody CommentCreateRequest request
    ){
        CommentResponse response = commentService.createComment(user.getId(), postId, request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("댓글 작성 완료", response));
    }


    @PostMapping("/api/comments/{commentId}/reply")
    public ResponseEntity<ApiResponse<CommentResponse>> createReply(
            @AuthenticationPrincipal User user,
            @PathVariable Long commentId,
            @Valid @RequestBody CommentCreateRequest request
    ){
        CommentResponse response = commentService.createReply(user.getId(), commentId, request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("댓글 작성 완료", response));
    }

}
