package org.sopt.dto;

import org.sopt.domain.Post;

public record PostResponse(Long id, String title, String content, String userName){

    public static PostResponse from(Post p) {
        return new PostResponse(p.getId(), p.getTitle(), p.getContent(), p.getUser().getName());
    }
}
