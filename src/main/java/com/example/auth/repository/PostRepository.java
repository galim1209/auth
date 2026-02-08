package com.example.auth.repository;

import com.example.auth.entity.Post;
import com.example.auth.entity.Visibility;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    // 소프트 삭제된 게시글 필터링
    // 삭제되지 않은 게시글이 존재하는지 확인
    boolean existsByIdAndIsDeletedFalse(Long id);
    boolean existsByIdAndUserIdAndIsDeletedFalse(Long id, Long userId);

    // 게시글 찾기
    Optional<Post> findByIdAndIsDeletedFalse(Long id);

    // 사용자가 올린 게시글 목록가져오기(최근순)
    Page<Post> findByUserIdAndIsDeletedFalseOrderByCreatedAtDesc(Long userId, Pageable pageable);

    // 게시글 조회(작성자 + 이미지) <=== JPA Query를 이용하여 데이터를 가져옴
    @Query("SELECT DISTINCT p FROM Post p JOIN FETCH p.user " +
        "LEFT JOIN FETCH p.images " +
        "WHERE p.id = :id AND p.isDeleted = false")
    Optional<Post> findByIdWithUserAndImages(@Param("id")Long id);


    @Query("SELECT p FROM Post p WHERE p.isDeleted = false AND p.visibility = :visibility " +
            "ORDER BY p.createdAt DESC")
    Page<Post> findByVisibilityAndIsDeletedFalse(
            @Param("visibility")Visibility visibility, Pageable pageable);


    // 조회수 증가시키기
    @Modifying
    @Query("UPDATE Post p SET p.viewCount = p.viewCount + 1 WHERE p.id = :postId")
    void incrementViewCount(@Param("postId") Long postId);

    // 좋아요 증가시키기
    @Modifying
    @Query("UPDATE Post p SET p.likeCount = p.likeCount+1 WHERE p.id = :postId")
    void incrementLikeCount(@Param("postId") Long postId);

    // 좋아요 감소시키기
    @Modifying
    @Query("UPDATE Post p SET p.likeCount = p.likeCount-1 WHERE p.id = :postId")
    void decrementLikeCount(@Param("postId") Long postId);

    // 댓글수 증가시키기
    @Modifying
    @Query("UPDATE Post p SET p.commentCount = p.commentCount+1 WHERE p.id = :postId")
    void incrementCommentCount(@Param("postId") Long postId);

    // 댓글수 감소시키기
    @Modifying
    @Query("UPDATE Post p SET p.commentCount = p.commentCount+1 WHERE p.id = :postId")
    void decrementCommentCount(@Param("postId") Long postId);
}
