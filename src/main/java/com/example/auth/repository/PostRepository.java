package com.example.auth.repository;

import com.example.auth.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostRepository  extends JpaRepository<Post, Long > {

    // 소프트 삭제된 게시글 필터링
    // 삭제되지 않은 게시글이 존재하는지 확인
    boolean existsByIdAndIsDeletedFalse(Long id);
    boolean existByIdAndUserIdANdIsDeletedFalse(Long id, Long userId);

    //  게시글 찾기
    Optional<Post> findByIdAndIsDeletesFalse(Long id);

    // 사용자가 올린 게시글 목록 가려오기
    Page<Post> findByUserIdAndIsDeletedFalseOrderByCreatedAtDesc(Long userId, Pageable pageable);



}
