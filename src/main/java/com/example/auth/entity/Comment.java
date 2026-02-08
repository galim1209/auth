package com.example.auth.entity;

import com.example.auth.dto.comment.CommentResponse;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "comments")
@Setter
@Getter
@NoArgsConstructor
@DynamicInsert
@DynamicUpdate
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Comment parent;

    @OneToMany(mappedBy = "parent")
    private List<Comment> children = new ArrayList<>();

    @Column(name = "content", length = 1000)
    private String content;

    @Column(name = "Like_count")
    private Integer likeCount;

    @Column(name = "is_deleted")
    private Boolean isDeleted;

    @CreationTimestamp  // Hibernate가 자동으로 생성시간 설정
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp    // Hibernate가 자동으로 업데이트 시간을 수정해줌
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public void softDelete(){
        isDeleted = true;
    }

    public static CommentResponse toCommentResponse(Comment comment){
        CommentResponse response = new CommentResponse();
        response.setId(comment.getId());
        response.setContent(comment.getContent());
        response.setUser(User.toCommentUser(comment.user));
        response.setParentId(comment.getParent()!=null ? comment.getParent().getId() : null);
        response.setReplyCount(comment.children.size());
        response.setLikeCount(comment.getLikeCount());
        response.setIsDeleted(comment.getIsDeleted());
        response.setCreatedAt(comment.getCreatedAt());
        response.setUpdatedAt(comment.getUpdatedAt());

        return response;
    }

    public static CommentResponse toCommentResponse(Comment comment, Integer replyCount){
        CommentResponse response = new CommentResponse();
        response.setId(comment.getId());
        response.setContent(comment.getContent());
        response.setUser(User.toCommentUser(comment.user));
        response.setParentId(comment.getParent()!=null ? comment.getParent().getId() : null);
        response.setReplyCount(replyCount);
        response.setLikeCount(comment.getLikeCount());
        response.setIsDeleted(comment.getIsDeleted());
        response.setCreatedAt(comment.getCreatedAt());
        response.setUpdatedAt(comment.getUpdatedAt());

        return response;
    }

}
