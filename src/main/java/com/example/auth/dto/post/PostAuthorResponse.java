package com.example.auth.dto.post;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostAuthorResponse {
    private Long id;
    private String name;
    private String profileImage;

}
