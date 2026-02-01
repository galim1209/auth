package com.example.auth.dto.post;

import com.example.auth.entity.Visibility;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostResponse {
    private Long id;
    private String content;
    private Visibility visibility;
    private Integer likeCount;
    private Integer commentCount;
    private Integer ViewCount;
    private PostAuthorResponse author;

    // 이미지 전달 될 자리
    private List<PostImageResponse> images;

    // 현재 조회한 사용자가 좋아요를 했는지 여부
    private Boolean isLiked;

    // Bookmark 자리
    // todo

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
