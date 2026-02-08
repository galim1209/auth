package com.example.auth.repository;

import com.example.auth.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    // 기본 조회
    /**
     * ID로 댓글 조회하기(삭제되지 않은 것만)
     */
    Optional<Comment> findByIdAndIsDeletedFalse(Long id);

    /**
     * 게시글의 모든 댓글 목록 조회(대댓글 포함)
     */
    @Query("SELECT c FROM Comment c " +
            "JOIN FETCH c.user " +
            "WHERE c.post.id = :postId AND c.isDeleted = false " +
            "ORDER BY c.createdAt ASC")
    Page<Comment> findAllCommentsByPostId(@Param("postId") Long postId, Pageable pageable);

    /**
     * 특정 댓글의 대댓글 조회
     */
    @Query("SELECT c FROM Comment c " +
            "JOIN FETCH c.user " +
            "WHERE c.parent.id = :parentId AND c.isDeleted = false " +
            "ORDER BY c.createdAt ASC")
    List<Comment> findRepliesByParentId(@Param("parentId") Long parentId);

    /**
     * 댓글 단건 조회 (작성자, 게시글 정보 포함)
     */
    @Query("SELECT c FROM Comment c " +
            "JOIN FETCH c.user " +
            "JOIN FETCH c.post " +
            "WHERE c.id = :id AND c.isDeleted = false")
    Optional<Comment> findByIdWithUserAndPost(@Param("id") Long id);

    // 대댓글 갯수 카운트
    @Query("SELECT COUNT(c) FROM Comment c " +
            "WHERE c.parent.id = :parentId AND c.isDeleted = false")
    Integer countRepliesByParentId(@Param("parentId") Long parentId);
}
