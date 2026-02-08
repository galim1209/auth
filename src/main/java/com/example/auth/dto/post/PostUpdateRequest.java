package com.example.auth.dto.post;

import com.example.auth.entity.Visibility;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostUpdateRequest {
    @Size(max = 5000, message = "게시글은 최대 5000자까지 작성 가능합니다.")
    private String content;

    private Visibility visibility;
}
