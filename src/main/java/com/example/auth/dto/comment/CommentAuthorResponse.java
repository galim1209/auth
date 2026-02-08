package com.example.auth.dto.comment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentAuthorResponse {
    private Long id;
    private String name;    // 작성자 정보
    private String profileImage;



}
