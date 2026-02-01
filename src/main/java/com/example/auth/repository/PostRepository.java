package com.example.auth.repository;

import com.example.auth.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    // 소프트 삭제된 게시글 필터링
    // 삭제되지 않은 게시글이 존재하는지 확인
    boolean existsByIdAndIsDeletedFalse(Long id);

    // 사용자가 올린 게시글 존재 확인
    boolean existsByIdAndUserIdAndIsDeletedFalse(Long id, Long userId);

    // 게시글 찾기
    Optional<Post> findByIdAndIsDeletedFalse(Long id);

    // 사용자가 올린 게시글 목록 가져오기(최근순)
    Page<Post> findByUserIdAndIsDeletedFalseOrderByCreatedAtDesc(Long userId, Pageable pageable);

    // 게시글 조회(작성자 + 이미지) <=== JPa Query를 이용하여 데이터를 가쟈옴
    @Query("SELECT DISTINCT p FROM Post p JOIN FETCH p.user " +
        "LEFT JOIN FETCH p.images " +
            "WHERE p.id = :id AND p.isDeleted = false")
    Optional<Post> findByIdWithUserAndImages(@Param("id") Long id);


}
