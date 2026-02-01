package com.example.auth.entity;

import com.example.auth.dto.post.PostImageResponse;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "post_images")
@Getter
@Setter
@NoArgsConstructor
@DynamicInsert
@DynamicUpdate
public class PostImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "thumbnail_url")
    private String thumbnailUrl;

    @Column(name = "sort_order")
    @ColumnDefault("0")
    private Integer sortOrder;

    @Column(name = "width")
    private Integer width;

    @Column(name = "height")
    private Integer height;

    @Column(name = "file_size")
    private Integer fileSize;

    @Enumerated(EnumType.STRING)
    @Column(name = "media_type")
    @ColumnDefault("IMAGE")
    private MediaType mediaType = MediaType.IMAGE;

    @CreationTimestamp  // Hibernate가 자동으로 생성시간 설정
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp    // Hibernate가 자동으로 업데이트 시간을 수정해줌
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public static PostImageResponse toDto(PostImage image){
        PostImageResponse res = new PostImageResponse();
        res.setId(image.getId());
        res.setImageUrl(image.getImageUrl());
        res.setThumbnailUrl(image.getThumbnailUrl());
        res.setSortOrder(image.getSortOrder());
        res.setWidth(image.getWidth());
        res.setHeight(image.getHeight());
        res.setMediaType(image.getMediaType());

        return res;
    }
}
