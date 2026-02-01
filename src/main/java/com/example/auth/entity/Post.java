package com.example.auth.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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


    private Integer likeCount;
    private Integer commentCount;
    private Integer viewCount;

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


    // 유틸리티 메서드

    // 이미지 추가
    public void addImage(PostImage image){
        images.add(image);
        image.setPost(this);

    }

    // 댓글 추가
    public void addComment(Comment comment){
        comments.add( comment );
        comment.setPost(this);
        commentCount++;
    }

    // 좋아요 증가
    public  void incrementLikeCount(){
        likeCount++;
    }

    // 좋아요 감소
    public void decrementLikeCount(){
        if (likeCount > 0){
            likeCount--;
        }
    }

    // 조회수 증가
    public void incrementViewCount(){
        viewCount++;
    }

    // 삭제됨 표시
    public void softDelete(){
        isDeleted = true;
    }
}


