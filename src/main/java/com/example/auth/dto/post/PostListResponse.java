package com.example.auth.dto.post;

import com.example.auth.entity.Visibility;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostListResponse {
    private Long id;
    private String content;
    private Visibility visibility;
    private String thumbnailUrl;
    private Integer imageCount;
    private Integer likeCount;
    private Integer commentCount;
    private PostAuthorResponse author;
    private LocalDateTime createdAt;
}
