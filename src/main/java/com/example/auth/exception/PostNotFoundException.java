package com.example.auth.exception;

public class PostNotFoundException extends RuntimeException {
    public PostNotFoundException(String message) {
        super(message);
    }

    public PostNotFoundException(Long postId) {
        super("게시글을 찾을 수 없습니다(post id: " + postId + ")");
    }
}
