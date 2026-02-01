package com.example.auth.repository;

import com.example.auth.entity.PostImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostImageRepository extends JpaRepository<PostImage, Long> {

    // 특정 게시글의 이미지 목록 조회
    List<PostImage> findByPostIdOrderBySortOrderAsc(Long postId);

    // 특정 게시글의 이미지 개수 조회
    Long countByPostId(Long postId);
}
