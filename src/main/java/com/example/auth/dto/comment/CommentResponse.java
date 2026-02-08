package com.example.auth.dto.comment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponse {
    // 댓글 id
    private Long id;
    // 댓글 내용
    private String content;

    private CommentAuthorResponse user;

    // 대댓글 지원을 위한 댓글의 ID
    private Long parentId;

    private Integer replyCount;
    private Integer likeCount;
    private Boolean isLiked;
    private Boolean isDeleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 이 댓글에 달린 댓글들 ...
    private List<CommentResponse> replies;

}
