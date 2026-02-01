package com.example.auth.dto.post;

import com.example.auth.entity.MediaType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostImageResponse {
    private Long id;
    private String imageUrl;
    private String thumbnailUrl;
    private Integer sortOrder;
    private Integer width;
    private Integer height;
    private MediaType mediaType;
}
