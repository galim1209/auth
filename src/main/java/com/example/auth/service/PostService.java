package com.example.auth.service;

import com.example.auth.dto.ImageUploadResponse;
import com.example.auth.dto.post.PostCreateRequest;
import com.example.auth.dto.post.PostResponse;
import com.example.auth.entity.MediaType;
import com.example.auth.entity.Post;
import com.example.auth.entity.PostImage;
import com.example.auth.entity.User;
import com.example.auth.repository.PostImageRepository;
import com.example.auth.repository.PostRepository;
import com.example.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostImageRepository postImageRepository;
    private final  ImageStorageService imageStorageService;

    @Transactional
    public PostResponse createPost(Long userId, PostCreateRequest request){
        return createPost(userId, request, null);
    }

    @Transactional
    public PostResponse createPost(Long userId, PostCreateRequest request, List<MultipartFile> images){
        // 게시글 작성
        System.out.println("게시글 작성 시작");;

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
        if (images != null && !images.isEmpty()){
            savePostImages(savedPost, images);
        }

        // 게시글과 함께 저장된 이미지 링크들의 정보가 있는 Post Entity 를 다시 불러와야 함
        savedPost = postRepository.findByIdAndIsDeletesFalse(postId).orElseThrow(
                ()->new RuntimeException("게시글이 없습니다")
        );

        return Post.toDto(savedPost, false, false);
    }

    private void savePostImages(Post post, List<MultipartFile> images){
        List<PostImage> postImages = new ArrayList<>();

        for(int i = 0; i<images.size(); i++){
            MultipartFile file = images.get(i);

            // 이미지 업로드 디렉토리에 저장하고 response를 받음
            ImageUploadResponse uploadResponse = imageStorageService.store(file);

            PostImage postImage = new PostImage();
            postImage.setPost(post);    // 게시물과 링킹
            postImage.setImageUrl(uploadResponse.getImageUrl());
            postImage.setThumbnailUrl(uploadResponse.getImageUrl());    // 나주엥 구현하기로 ..
            postImage.setSortOrder(i);
            postImage.setFileSize(uploadResponse.getFileSize().intValue());
            postImage.setMediaType(MediaType.IMAGE);    // IMAGE TYPE만 현재는 지원함..(나주엥 알아서 구현)

            postImages.add(postImage);
        }

        // Image 일관 저장
        postImageRepository.saveAll(postImages);
    }
}
