package com.example.auth.entity;

import com.example.auth.dto.post.PostAuthorResponse;
import com.example.auth.dto.post.PostImageResponse;
import com.example.auth.dto.post.PostListResponse;
import com.example.auth.dto.post.PostResponse;
import jakarta.persistence.*;
import jakarta.persistence.CascadeType;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "post", indexes = {
        // 특정 사용자의 게시글 조회용 인덱스
        @Index(name = "idx_user_id", columnList = "user_id"),
        // 최신순 정렬 조회용 인덱스
        @Index(name = "idx_created_at", columnList = "created_at DESC"),
        // 특정 사용자의 최신 게시글 조회용 복합 인덱스
        @Index(name = "idx_user_created", columnList = "user_id, created_at DESC")
})
@Getter
@Setter
@NoArgsConstructor
@DynamicInsert
@DynamicUpdate
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    private Visibility visibility = Visibility.PUBLIC;

    @Column(name = "like_count")
    @ColumnDefault("0")
    private Integer likeCount;

    @Column(name = "comment_count")
    @ColumnDefault("0")
    private Integer commentCount;

    @Column(name = "view_count")
    @ColumnDefault("0")
    private Integer viewCount;

    @Column(name = "is_deleted")
    @ColumnDefault("false")
    private Boolean isDeleted = false;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostImage> images = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<Comment> comments = new ArrayList<>();

    @CreationTimestamp  // Hibernate가 자동으로 생성시간 설정
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp    // Hibernate가 자동으로 업데이트 시간을 수정해줌
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /// ///////////////
    /* 유틸리티 메서드 */
    /// ///////////////

    // 이미지 추가
    public void addImage(PostImage image) {
        images.add(image);
        image.setPost(this);
    }

    // 댓글 추가
    public void addComment(Comment comment) {
        comments.add( comment );
        comment.setPost(this);
        commentCount++;
    }

    // 좋아요 증가
    public void incrementLikeCount() {
        likeCount++;
    }

    // 좋아요 감소
    public void decrementLikeCount() {
        if (likeCount > 0) {
            likeCount--;
        }
    }

    // 조회수 증가
    public void incrementViewCount() {
        viewCount++;
    }

    // 삭제됨 표시
    public void softDelete() {
        isDeleted = true;
    }

    // Entity -> DTO
    public static PostResponse toDto(Post post, boolean isLiked, boolean isBookmarked) {
        PostResponse res = new PostResponse();
        res.setId(post.getId());
        res.setContent(post.getContent());
        res.setVisibility(post.getVisibility());
        res.setLikeCount(post.getLikeCount());
        res.setCommentCount(post.getCommentCount());
        res.setViewCount(post.getViewCount());
        res.setAuthor(PostAuthorResponse.from(post.getUser()));
        res.setImages(post.getImages().stream().map(PostImage::toDto).collect(Collectors.toList()));
        res.setIsLiked(isLiked);
        // bookmark todo
        res.setCreatedAt(post.getCreatedAt());
        res.setUpdatedAt(post.getUpdatedAt());

        return res;
    }


    public static PostListResponse toListDto(Post post) {
        String contentPreview = post.getContent();
        if (contentPreview != null && contentPreview.length() > 100) {
            contentPreview = contentPreview.substring(0, 100) + "...";
        }

        String thumbnailUrl = null;
        if (!post.getImages().isEmpty()) {
            thumbnailUrl = post.getImages().get(0).getThumbnailUrl();
        }

        PostListResponse dto = new PostListResponse();
        dto.setId(post.getId());
        dto.setContent(contentPreview);
        dto.setVisibility(post.getVisibility());
        dto.setThumbnailUrl(thumbnailUrl);
        dto.setImageCount(post.getImages().size());
        dto.setLikeCount(post.getLikeCount());
        dto.setCommentCount(post.getCommentCount());
        dto.setAuthor(PostAuthorResponse.from(post.getUser()));
        dto.setCreatedAt(post.getCreatedAt());

        return dto;
    }
}
