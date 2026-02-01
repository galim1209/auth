package com.example.auth.dto.post;

import com.example.auth.entity.User;
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

    public static PostAuthorResponse from(User user){
        PostAuthorResponse author = new PostAuthorResponse();
        author.setId(user.getId());
        author.setName(user.getNickName());
        author.setProfileImage(user.getProfileImage());

        return author;
    }
}
