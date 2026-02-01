package com.example.auth.service;

import com.example.auth.dto.post.PostCreateRequest;
import com.example.auth.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;

    @Transactional
    public PostRepository createPost(Long userId, PostCreateRequest request){

    }
}
